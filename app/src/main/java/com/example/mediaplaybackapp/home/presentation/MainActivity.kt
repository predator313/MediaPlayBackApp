package com.example.mediaplaybackapp.home.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaplaybackapp.home.presentation.components.HomeScreen
import com.example.mediaplaybackapp.ui.theme.MediaPlaybackAppTheme
import com.example.mediaplaybackapp.utils.navigateViaIntent

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            MediaPlaybackAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val homeUiState by homeViewModel.homeUiStateFlow.collectAsStateWithLifecycle()
                    HomeScreen(
                        homeUiState = homeUiState,
                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                        onPlayIconClicked = {
                            startActivity(
                                navigateViaIntent(
                                    context = this@MainActivity,
                                    streamUrl = homeUiState.streamUrl
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}