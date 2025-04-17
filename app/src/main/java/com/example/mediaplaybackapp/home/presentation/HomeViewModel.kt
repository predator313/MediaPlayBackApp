package com.example.mediaplaybackapp.home.presentation

import androidx.lifecycle.ViewModel
import com.example.mediaplaybackapp.home.domain.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel (): ViewModel() {
    private val _homeUiStateFlow:MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val homeUiStateFlow = _homeUiStateFlow.asStateFlow()
}