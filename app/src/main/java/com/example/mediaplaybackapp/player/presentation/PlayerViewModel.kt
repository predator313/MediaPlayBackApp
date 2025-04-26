package com.example.mediaplaybackapp.player.presentation

import android.net.Uri
import android.view.Surface
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaplaybackapp.player.domain.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(private val exoPlayer: ExoPlayer): ViewModel() {
    private val _playerUiStateFlow = MutableStateFlow(PlayerUiState())
    val playerUiStateFlow = _playerUiStateFlow.asStateFlow()

    fun setStreamUrl(streamUrl: String) {
        val mediaItem = MediaItem.Builder().apply {
            setUri(Uri.parse(streamUrl))
        }
        exoPlayer.setMediaItem(mediaItem.build())
    }

    fun setVideoSurface(surface: Surface) {
        exoPlayer.setVideoSurface(surface)
    }

    fun startPlayback() {
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun clearVideoSurface() {
        exoPlayer.setVideoSurface(null)
    }

    fun stopPlayback() {
        exoPlayer.stop()
    }
}