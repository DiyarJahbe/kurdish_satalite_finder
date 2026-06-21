package com.learnkt.kurdish_satalite_finder.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.domain.repository.SatelliteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SatelliteRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.seedDatabase()
        }
    }
}
