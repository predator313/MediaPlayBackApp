package com.example.mediaplaybackapp.player.presentation

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mediaplaybackapp.player.presentation.action.PlayerAction
import com.example.mediaplaybackapp.player.presentation.components.VideoPlayer
import com.example.mediaplaybackapp.ui.theme.MediaPlaybackAppTheme
import com.example.mediaplaybackapp.utils.STREAM_URL_KEY
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class PlayerActivity : ComponentActivity() {
    private val playerViewModel: PlayerViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val streamUrl = intent.getStringExtra(STREAM_URL_KEY)?:throw IllegalArgumentException("not valid url")
        Timber.tag("hello").e("stream url $streamUrl")
        playerViewModel.handlePlayerAction(PlayerAction.Init(streamUrl))
        setContent {
            val playerUiState by playerViewModel.playerUiStateFlow.collectAsStateWithLifecycle()
            val window = this.window
            val windowInsertController = WindowCompat.getInsetsController(window, window.decorView)
            LaunchedEffect(
                key1 = playerUiState.isFullScreen
            ) {
                    if (playerUiState.isFullScreen) {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        windowInsertController.hide(WindowInsetsCompat.Type.systemBars())
                    } else {
                        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        windowInsertController.show(WindowInsetsCompat.Type.systemBars())
                    }
            }
            MediaPlaybackAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VideoPlayer(
                        modifier = Modifier.fillMaxSize()
                            .padding(innerPadding),
                        playerUiState = playerUiState,
                        onCollapsedClick = playerViewModel::exitFullScreen,
                        onExpendClick = playerViewModel::enterFullScreen,
                        onVideoSurfaceClick = playerViewModel::showPlayerControl,
                        onPlayerAction = playerViewModel::handlePlayerAction
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        playerViewModel.handlePlayerAction(PlayerAction.Start(null))
    }

    override fun onStop() {
        super.onStop()
        playerViewModel.handlePlayerAction(PlayerAction.Stop)
    }
}