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
import androidx.core.net.toUri
import com.example.mediaplaybackapp.player.domain.PlaybackState
import com.example.mediaplaybackapp.player.presentation.action.PlayerAction

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

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                _playerUiStateFlow.update {
                    it.copy(
                        playbackState = PlaybackState.PLAYING
                    )
                }
            } else if (exoPlayer.playbackState == Player.STATE_READY) {
                _playerUiStateFlow.update {
                    it.copy(
                        playbackState = PlaybackState.PAUSED
                    )
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val state = when (playbackState) {
                Player.STATE_IDLE -> {
                    if (exoPlayer.playerError != null) {
                        PlaybackState.ERROR
                    } else {
                        PlaybackState.IDLE
                    }
                }

                Player.STATE_BUFFERING -> {
                    PlaybackState.BUFFERING
                }

                Player.STATE_READY -> {
                    if (exoPlayer.playWhenReady) {
                        PlaybackState.PLAYING
                    } else {
                        PlaybackState.PAUSED
                    }
                }

                Player.STATE_ENDED -> {
                    PlaybackState.COMPLETED
                }

                else -> {
                    PlaybackState.IDLE
                }
            }
            _playerUiStateFlow.update {
                it.copy(playbackState = state)
            }

            if (state == PlaybackState.ERROR) showPlayerControl()
        }
    }

    init {
        exoPlayer.addListener(playerEventListener)
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

    fun handlePlayerAction(action: PlayerAction) {
        when (action) {
            is PlayerAction.AttachSurface -> {
                exoPlayer.setVideoSurface(action.surface)
            }

            is PlayerAction.DetachSurface -> {
                exoPlayer.setVideoSurface(null)
            }

            is PlayerAction.FastForward -> {
                exoPlayer.seekTo(exoPlayer.currentPosition + action.amountInMs)
            }

            is PlayerAction.Init -> {
                val mediaItem = MediaItem.Builder().apply {
                    setUri(action.streamUrl.toUri())
                }
                exoPlayer.setMediaItem(mediaItem.build())
            }

            is PlayerAction.Pause -> {
                exoPlayer.pause()
            }

            is PlayerAction.Resume -> {
                exoPlayer.play()
            }

            is PlayerAction.Rewind -> {
                exoPlayer.seekTo(exoPlayer.currentPosition - action.amountInMs)
            }

            is PlayerAction.Seek -> {
                exoPlayer.seekTo(action.amountInMs)
            }

            is PlayerAction.Start -> {
                exoPlayer.apply {
                    prepare()
                    play()
                    action.positionInMs?.let {
                        seekTo(it)
                    }
                }
            }

            is PlayerAction.Stop -> {
                exoPlayer.stop()
            }
        }
    }
}