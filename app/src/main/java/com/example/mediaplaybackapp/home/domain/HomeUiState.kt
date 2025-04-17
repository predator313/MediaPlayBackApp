package com.example.mediaplaybackapp.home.domain

import com.example.mediaplaybackapp.R

data class HomeUiState(
    val coverImageResourceId: Int = R.drawable.home_icon,
    val description: String = "The film’s premise is about a group of warriors and scientists, who gathered at the “Oude " +
            "Kerk” in Amsterdam to stage a crucial event from the past, in a desperate attempt to rescue " +
            "the world from destructive robots.",
    val streamUrl: String = "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd",
    val attributionText: String = "(CC) Blender Foundation | mango.blender.org"
)
