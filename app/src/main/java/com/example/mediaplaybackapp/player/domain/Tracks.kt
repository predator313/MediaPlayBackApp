package com.example.mediaplaybackapp.player.domain

data class VideoTrack(
    val width: Int,
    val height: Int,
) {
    val displayName: String
        get() = when(this) {
            AUTO -> "Auto"
            else -> "$width $height"
        }
    companion object {
        val AUTO = VideoTrack(0, 0)
    }
}

data class AudioTrack(
    val language: String
) {
    val displayName: String
        get() = when(this) {
            AUTO -> "Auto"
            NONE -> "None"
            else -> language
        }
    companion object {
        val AUTO = AudioTrack("Auto")
        val NONE = AudioTrack("None")
    }
}

data class SubtitleTrack(
    val language: String
) {
    val displayName: String
        get() = when(this) {
            AUTO -> "Auto"
            NONE -> "None"
            else -> language
        }
    companion object {
        val AUTO = SubtitleTrack("Auto")
        val NONE = SubtitleTrack("None")
    }
}
