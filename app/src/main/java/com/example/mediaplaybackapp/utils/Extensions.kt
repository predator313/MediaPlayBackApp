package com.example.mediaplaybackapp.utils

import com.example.mediaplaybackapp.player.domain.PlaybackState

fun PlaybackState.isReady(): Boolean {
    return  this == PlaybackState.PLAYING || this ==  PlaybackState.PAUSED
}