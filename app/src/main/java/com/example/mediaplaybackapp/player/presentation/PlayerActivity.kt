package com.example.mediaplaybackapp.player.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
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
        playerViewModel.setStreamUrl(streamUrl)
        setContent {
            MediaPlaybackAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    VideoPlayer(
                        modifier = Modifier.padding(innerPadding),
                        setSurface = playerViewModel::setVideoSurface,
                        clearSurface = playerViewModel::clearVideoSurface,
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        playerViewModel.startPlayback()
    }
}