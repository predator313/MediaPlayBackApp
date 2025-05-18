package com.example.mediaplaybackapp.player.presentation

import android.net.Uri
import android.view.Surface
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaplaybackapp.player.domain.PlayerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer
) : ViewModel() {
    private val _playerUiStateFlow = MutableStateFlow(PlayerUiState())
    val playerUiStateFlow = _playerUiStateFlow.asStateFlow()


    private val playerEventListener: Player.Listener = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            if (videoSize != VideoSize.UNKNOWN) {
                val videoWidth = videoSize.width
                val videoHeight = videoSize.height / videoSize.pixelWidthHeightRatio
                val videoAspectRatio = videoWidth / videoHeight
                _playerUiStateFlow.update {
                    it.copy(videoAspectRation = videoAspectRatio)
                }
            }

        }
    }

    init {
        exoPlayer.addListener(playerEventListener)
    }

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

    fun enterFullScreen() {
        _playerUiStateFlow.update {
            it.copy(
                isFullScreen = true,
                showPlayerControl = false
            )
        }
    }

    fun exitFullScreen() {
        _playerUiStateFlow.update {
            it.copy(
                isFullScreen = false,
                showPlayerControl = false
            )
        }
    }

    fun showPlayerControl() {
        _playerUiStateFlow.update {
            if (it.showPlayerControl) {
                it.copy(showPlayerControl = false)
            } else it.copy(showPlayerControl = true)
        }
    }
}