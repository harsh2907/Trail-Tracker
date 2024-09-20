package com.example.trailtracker.mainScreen.domain.repositories

import android.net.Uri
import android.util.Log
import com.example.trailtracker.mainScreen.domain.models.User
import com.example.trailtracker.utils.Constants
import com.example.trailtracker.utils.RequestState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await

class FirebaseUserRepository {

    private val firestore = Firebase.firestore
    private val userRef = firestore.collection(Constants.Firebase.User.USER_REF)
    private val storageRef = Firebase.storage.reference

    private val auth = Firebase.auth

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // Store the listener registration
    private var userListenerRegistration: ListenerRegistration? = null

    fun getCurrentUser() {
        Firebase.auth.uid?.let { currentUserId ->
            // Stop any previous listener to avoid multiple listeners
            userListenerRegistration?.remove()

            userListenerRegistration = userRef.document(currentUserId)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        // Handle error
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val user = snapshot.toObject(User::class.java)
                        _currentUser.update { user }
                    }
                }
        }
    }


    init {
        getCurrentUser()
    }

    fun signOut() = runCatching {
        Firebase.auth.signOut()
        // Only remove the listener and update state if sign-out succeeds
        userListenerRegistration?.remove()
        _currentUser.update { null }
    }.onFailure { exception ->
        // Log the error or notify the user about the failure
        Log.e("FirebaseUserRepository", "Sign-out failed", exception)
        // Optionally, handle further actions like showing a message to the user
    }


    suspend fun addOrUpdateUser(user: User): Flow<RequestState<Unit>> = flow {
        emit(RequestState.Loading)
        try {

            userRef.document(user.userId).set(user).await()
            emit(RequestState.Success(data = Unit))
            _currentUser.update { user }

        } catch (e: Exception) {
            emit(RequestState.Error(e.message ?: "oops, an unknown error occurred"))
            e.printStackTrace()
            println("Add user Error: " + e.message)
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            userRef.document(userId).get().await().toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun updateUserProfile(
        imageUri: Uri,
        onSuccess: (imageUrl: String) -> Unit,
        onError: (String) -> Unit
    ) {
        val user = currentUser.value!!
        val profileUrl = addImageToStorage(userId = user.userId, fileUri = imageUri)

        if (profileUrl != null) {
            addOrUpdateUser(user.copy(profileImageUrl = profileUrl)).collectLatest { res ->
                when {
                    res.isSuccess() -> onSuccess(profileUrl)
                    res.isError() -> onError(res.getErrorMessage())
                }
            }
        } else {
            onError("Can't update profile image, please try again.")
        }
    }

    suspend fun addImageToStorage(
        userId: String,
        fileId: String = "profile_image",
        fileUri: Uri?
    ): String? {
        return runCatching {
            if (fileUri == null) return null

            val fileRef = storageRef.child(Constants.Firebase.User.USER_REF)
                .child(userId)
                .child(fileId)

            fileRef.putFile(fileUri).await()
            fileRef.downloadUrl.await().toString()
        }.getOrNull()
    }

    suspend fun deleteUser(
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        try {
            auth.currentUser?.let { user ->
                user.delete().await()
                userRef.document(user.uid).delete().await()
                onSuccess()
            }
        } catch (e: Exception) {
            Log.e("DeleteUser", "Failed to delete user: ${e.message}")
            onError(e)
        }
    }

    suspend fun getUser(userId: String): User? {
        return userRef.document(userId).get().await().toObject(User::class.java)
    }

}