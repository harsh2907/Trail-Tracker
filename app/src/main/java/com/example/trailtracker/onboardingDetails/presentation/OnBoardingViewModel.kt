package com.example.trailtracker.onboardingDetails.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.datastore.DataStoreUtils
import com.example.trailtracker.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val dataStoreUtils: DataStoreUtils
) : ViewModel() {

    private val _startDestination = MutableStateFlow(Screens.IdleScreen.route)
    val startDestination = _startDestination.asStateFlow()

    val userDetails = dataStoreUtils.getDetails().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        Pair("", 0)
    )

    fun setDetails(name: String, weight: Int) {
        viewModelScope.launch {
            dataStoreUtils.setDetails(name, weight)
        }
    }


    init {
        viewModelScope.launch {
            userDetails.collectLatest { (name,weight)->
                if (name.isNotEmpty() || weight != 0) {
                    _startDestination.update { Screens.MainScreen.route }
                } else {
                    delay(1000L)
                    _startDestination.update{ Screens.OnBoardingScreen.route }
                }
            }
        }


    }




}