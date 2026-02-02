package com.example.mediaplaybackapp.player.presentation.action

import android.view.Surface
import com.example.mediaplaybackapp.player.domain.AudioTrack
import com.example.mediaplaybackapp.player.domain.SubtitleTrack
import com.example.mediaplaybackapp.player.domain.VideoTrack

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
    data class SetVideoTrack(val track: VideoTrack) : PlayerAction
    data class SetAudioTrack(val track: AudioTrack) : PlayerAction
    data class SetSubtitleTrack(val track: SubtitleTrack) : PlayerAction
}