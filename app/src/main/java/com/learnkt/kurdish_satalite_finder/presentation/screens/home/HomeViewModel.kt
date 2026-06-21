package com.learnkt.kurdish_satalite_finder.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learnkt.kurdish_satalite_finder.domain.usecase.SeedSatellitesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val seedSatellitesUseCase: SeedSatellitesUseCase
) : ViewModel() {

    init {
        viewModelScope.launch {
            seedSatellitesUseCase()
        }
    }
}
