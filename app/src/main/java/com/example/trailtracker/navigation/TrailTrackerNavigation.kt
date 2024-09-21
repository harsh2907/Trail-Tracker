package com.example.trailtracker.navigation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.trailtracker.R
import com.example.trailtracker.mainScreen.presentation.MainScreen
import com.example.trailtracker.onboarding.screens.OnBoardingScreen
import com.example.trailtracker.onboarding.AuthenticationViewModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch

@Composable
fun TrailTrackerNavigation(
    navController: NavHostController,
    startDestination: String
) {

    NavHost(navController = navController, startDestination = startDestination) {

        composable(route = Screens.IdleScreen.route) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            )
        }

        composable(route = Screens.OnBoardingScreen.route) {

            val authViewModel: AuthenticationViewModel = hiltViewModel()
            val authState by authViewModel.authState.collectAsStateWithLifecycle()
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            val clientId = context.getText(R.string.default_web_client_id).toString()

            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setServerClientId(clientId)
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
                .build()

            val credentialManager = CredentialManager.create(context)

            val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val signUpWithGoogle = {
                coroutineScope.launch {
                    try {
                        val result = credentialManager.getCredential(
                            context = context,
                            request = request
                        )

                        authViewModel.handleGoogleAuthentication(result)

                    } catch (e: Exception) {
                        Toast.makeText(context, "An unknown error occurred.Please try again.", Toast.LENGTH_SHORT).show()

                        Log.e("Trail-Tracker-Auth", e.message, e)
                    }
                }

            }

            LaunchedEffect(key1 = authState) {
                if (authState.isSignInSuccessful) {
                    navController.popBackStack()
                    navController.navigate(Screens.MainScreen.route)
                } else {
                    Log.e("Auth",authState.error.toString())
                    Toast.makeText(context, authState.error , Toast.LENGTH_SHORT).show()
                }
            }

            OnBoardingScreen(
                isLoading = authState.isLoading,
                onClick = {
                    signUpWithGoogle()
                }
            )

        }


        composable(route = Screens.MainScreen.route) {
            MainScreen()
        }
    }
}