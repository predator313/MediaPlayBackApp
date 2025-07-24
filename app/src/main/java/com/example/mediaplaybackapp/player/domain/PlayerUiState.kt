package com.example.mediaplaybackapp.player.domain

data class PlayerUiState(
    val videoAspectRation: Float = 16.0f / 9.0f,
    val isFullScreen: Boolean = false,
    val showPlayerControl: Boolean = false,
    val playbackState: PlaybackState = PlaybackState.IDLE
)
