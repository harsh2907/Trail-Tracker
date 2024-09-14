package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.domain.models.Run
import com.example.trailtracker.mainScreen.domain.repositories.FirebaseUserRepository
import com.example.trailtracker.mainScreen.domain.repositories.RunRepository
import com.example.trailtracker.mainScreen.domain.usecases.SortRunsUseCase
import com.example.trailtracker.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val runRepository: RunRepository,
    firebaseUserRepository: FirebaseUserRepository,
    private val sortRunsUseCase: SortRunsUseCase
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.DATE)

    private val _allRunsState = MutableStateFlow(AllSessionsState())
    val allRunsState = _allRunsState.asStateFlow()

    val currentUser = firebaseUserRepository.currentUser


    init {
        viewModelScope.launch {
            _allRunsState.update { it.copy(isLoading = true) }
            sortRunsUseCase(_sortType.value).collectLatest { runs ->
                _allRunsState.update { it.copy(runSessions = runs, isLoading = false) }
            }
        }
    }

    fun onSortTypeChanged(sortType: SortType) {
        viewModelScope.launch {
            _allRunsState.update { it.copy(isLoading = true) }

            sortRunsUseCase(sortType).collectLatest { sortedRuns ->
                _allRunsState.update { it.copy(runSessions = sortedRuns, isLoading = false) }
            }
        }
    }

    fun deleteRun(run: Run) {
        viewModelScope.launch {
            runRepository.deleteRun(run)
        }
    }


}