package com.example.mediaplaybackapp.player.domain

data class TrackSelectionUiModel(
    val selectedVideoTrack: VideoTrack,
    val videoTracks: List<VideoTrack>,
    val selectedAudioTrack: AudioTrack,
    val audioTracks: List<AudioTrack>,
    val selectedSubtitleTrack: SubtitleTrack,
    val subtitleTracks: List<SubtitleTrack>
)
