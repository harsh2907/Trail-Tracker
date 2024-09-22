package com.example.trailtracker.mainScreen.presentation.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.data.FirebaseRunRepository
import com.example.trailtracker.mainScreen.domain.repositories.FirebaseUserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseUserRepository: FirebaseUserRepository,
    private val firebaseRunRepository: FirebaseRunRepository
) : ViewModel() {

    val currentUser = firebaseUserRepository.currentUser

    fun getTotalTimeSpend() = firebaseRunRepository.getTotalDurationForSessions()
    fun getAverageSpeed() = firebaseRunRepository.getTotalAverageSpeedForSessions()
    fun getTotalDistance() = firebaseRunRepository.getTotalDistanceCoveredForSessions()
    fun getTotalCalories() = firebaseRunRepository.getTotalCaloriesBurnedForSessions()


    fun updateUserProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            firebaseUserRepository.updateUserProfile(
                imageUri = imageUri,
                onSuccess = {profileImageUrl->
                    Log.e("ProfileViewModel","Profile Updated: $profileImageUrl")
                },
                onError = {error->
                    Log.e("ProfileViewModel",error)
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        firebaseRunRepository.removeListener()
    }

}