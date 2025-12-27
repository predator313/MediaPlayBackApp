package com.example.mediaplaybackapp.player.domain

import com.example.mediaplaybackapp.R

data class PlayerUiState(
    val videoAspectRation: Float = 16.0f / 9.0f,
    val isFullScreen: Boolean = false,
    val showPlayerControl: Boolean = false,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val timeLineUiModel: TimeLineUiModel? = null,
    val showPlaceholderImg: Int? = R.drawable.home_icon,
    val trackSelection: TrackSelectionUiModel? = null
)

data class TimeLineUiModel(
    val durationsInMs: Long,
    val currentPositionInMs: Long,
    val bufferedPositionInMs: Long
)
