package com.example.mediaplaybackapp.player.presentation.action

import android.view.Surface

sealed interface PlayerAction {
    data class Init(val streamUrl: String) : PlayerAction
    data object Pause : PlayerAction
    data class Start(val positionInMs: Long? = null) : PlayerAction
    data object Stop : PlayerAction
    data object Resume: PlayerAction
    data class Rewind(val amountInMs: Long) : PlayerAction
    data class FastForward(val amountInMs: Long) : PlayerAction
    data class Seek(val amountInMs: Long) : PlayerAction
    data class AttachSurface(val surface: Surface) : PlayerAction
    data object DetachSurface : PlayerAction
}