package com.example.trailtracker.onboarding

import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.domain.models.User
import com.example.trailtracker.mainScreen.domain.repositories.FirebaseUserRepository
import com.example.trailtracker.navigation.Screens
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository
) : ViewModel() {

    private val _startDestination = MutableStateFlow(Screens.IdleScreen.route)
    val startDestination = _startDestination.asStateFlow()

    private val _authState = MutableStateFlow(AuthState())
    val authState = _authState.asStateFlow()

    init {
        _startDestination.update {
            if (Firebase.auth.uid == null) Screens.OnBoardingScreen.route else Screens.MainScreen.route
        }
    }


    fun handleGoogleAuthentication(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        _authState.update { it.copy(isLoading = true) }

        when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdToken =
                            GoogleIdTokenCredential.createFrom(credential.data).idToken

                        signupWithGoogle(googleIdToken)

                    } catch (e: Exception) {
                        _authState.update {
                            it.copy(
                                isSignInSuccessful = false,
                                isLoading = false,
                                error = "Failed to login, please try again."
                            )
                        }
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                    _authState.update {
                        it.copy(
                            isSignInSuccessful = false,
                            isLoading = false,
                            error = "Failed to login, please try again."
                        )
                    }
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
                _authState.update {
                    it.copy(
                        isSignInSuccessful = false,
                        isLoading = false,
                        error = "Failed to login, please try again."
                    )
                }
            }
        }
    }

    private fun signupWithGoogle(token: String) {
        viewModelScope.launch {
            val result = authenticationWithGoogleUsingToken(token)
            when {
                !result.error.isNullOrEmpty() -> _authState.update {
                    it.copy(
                        isSignInSuccessful = false, error = result.error, isLoading = false
                    )
                }

                result.data != null -> {
                    if (result.isNewUser) {
                        addUserToFirebase(result.data)
                    } else {
                        _authState.update {
                            it.copy(
                                isSignInSuccessful = true,
                                isLoading = false,
                                error = "",
                                isNewUser = false
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun authenticationWithGoogleUsingToken(googleIdToken: String): SignInResponse {
        try {
            val auth = Firebase.auth
            val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
            val firebaseUser =
                auth.signInWithCredential(googleCredentials).await().user ?: return SignInResponse(
                    data = null,
                    error = "An unknown error occurred while signing in with google. " + "Please try again later"
                )

            if (firebaseUser.email.isNullOrEmpty()) {
                return SignInResponse(
                    data = null,
                    error = "No email is linked with this email. Please try a different account"
                )
            }


            var user = firebaseUserRepository.getUserById(firebaseUser.uid)
            if (user == null) {
                user = firebaseUser.run {
                    User(
                        userId = uid,
                        username = displayName ?: "",
                        email = email ?: "",
                        profileImageUrl = photoUrl?.toString() ?: ""
                    )
                }

                return SignInResponse(
                    data = user, error = null, isNewUser = true
                )
            }


            return SignInResponse(
                data = user, error = null, isNewUser = false
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return SignInResponse(
                data = null, error = e.message
            )
        }
    }

    private fun addUserToFirebase(user: User) {
        viewModelScope.launch {
            firebaseUserRepository.addOrUpdateUser(user).collectLatest { res ->
                if (res.isSuccess()) {
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            isSignInSuccessful = true,
                            error = "",
                            user = user
                        )
                    }
                }

                if (res.isError()) {
                    _authState.update {
                        it.copy(
                            isLoading = false,
                            isSignInSuccessful = false,
                            error = res.getErrorMessage()
                        )
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = "AuthenticationViewModel"
    }

}