package com.example.trailtracker.mainScreen.presentation.screens.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trailtracker.mainScreen.domain.models.Run
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
    private val sortRunsUseCase: SortRunsUseCase
):ViewModel() {

    private val _sortType = MutableStateFlow(SortType.DATE)
    val sortType = _sortType.asStateFlow()

    private val _allRuns = MutableStateFlow<List<Run>>(emptyList())
    val allRuns = _allRuns.asStateFlow()


    fun getAllRuns(){
        viewModelScope.launch {
            sortRunsUseCase(_sortType.value).collectLatest {runs->
                _allRuns.update { runs }
            }
        }
    }

    fun deleteRun(run:Run){
        viewModelScope.launch {
            runRepository.deleteRun(run)
        }
    }


}