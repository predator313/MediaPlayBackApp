package com.example.mediaplaybackapp.player.presentation.components

import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.text.Cue
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.SubtitleView

@OptIn(UnstableApi::class)
@Composable
fun SubtitleView(
    cues: List<Cue>,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = {
            SubtitleView(it)
        },
        update = {
            it.setCues(cues)
        }
    )
}