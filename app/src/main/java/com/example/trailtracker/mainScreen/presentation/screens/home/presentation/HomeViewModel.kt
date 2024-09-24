package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.trailtracker.mainScreen.data.FirebaseRunRepository
import com.example.trailtracker.mainScreen.domain.models.RunItem
import com.example.trailtracker.mainScreen.domain.repositories.FirebaseUserRepository
import com.example.trailtracker.mainScreen.domain.usecases.SortRunsUseCase
import com.example.trailtracker.utils.SortType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val firebaseRunRepository: FirebaseRunRepository,
    private val firebaseUserRepository: FirebaseUserRepository,
    private val sortRunsUseCase: SortRunsUseCase,
    private val workManager: WorkManager
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.DATE)

    private val _allRunsState = MutableStateFlow(AllSessionsState())
    val allRunsState = _allRunsState.asStateFlow()

    val currentUser = firebaseUserRepository.currentUser

    fun addRunItem(runItem: RunItem) {
        _allRunsState.update {
            it.copy(
                runSessions = listOf(runItem) + it.runSessions
            )
        }
    }


    init {
        viewModelScope.launch {
            firebaseUserRepository.getCurrentUser()

            _allRunsState.update { it.copy(isLoading = true) }
            sortRunsUseCase(_sortType.value).collectLatest { runs ->
                _allRunsState.update {
                    it.copy(
                        runSessions = runs,
                        isLoading = false
                    )
                }
            }

        }
    }

    fun onRefresh() {
        viewModelScope.launch {
            _allRunsState.update { it.copy(isLoading = true) }

            firebaseRunRepository.getAllRunsSortedByDate().collectLatest { result ->
                result
                    .onSuccess { runs ->
                        delay(1000)
                        _allRunsState.update {
                            it.copy(
                                isLoading = false,
                                error = "",
                                runSessions = runs.map { run -> run.toRunItem() })
                        }
                    }
                    .onFailure {t->
                        delay(1000)
                        _allRunsState.update { it.copy(isLoading = false, error = t.message ?: "An error occurred, please refresh.") }
                    }
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

    fun deleteRun(
        runItem: RunItem,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _allRunsState.update {
                it.copy(
                    runSessions = it.runSessions.toMutableList().apply { remove(runItem) }
                )
            }

            firebaseRunRepository.deleteRun(runItem.id)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { error ->
                    _allRunsState.update {
                        it.copy(
                            runSessions = it.runSessions.toMutableList()
                                .apply { remove(runItem) }
                        )
                    }
                    onError(error.message ?: "Oops,an unknown error occurred")
                }
        }
    }

    fun signOut(onSuccess: () -> Unit, onError: (String) -> Unit) {
        firebaseUserRepository.signOut()
            .onSuccess { onSuccess() }
            .onFailure {
                onError(it.message ?: "Oops,an unknown error occurred")
            }
    }


}