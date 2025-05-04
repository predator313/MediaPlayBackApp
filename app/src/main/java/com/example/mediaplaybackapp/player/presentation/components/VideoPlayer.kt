package com.example.mediaplaybackapp.player.presentation.components

import android.view.Surface
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.mediaplaybackapp.player.domain.PlayerUiState

@Composable
fun VideoPlayer(
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
    setSurface: (Surface) -> Unit,
    clearSurface: () -> Unit,
) {
    Box(
        modifier = modifier.aspectRatio(playerUiState.videoAspectRation),
       contentAlignment = Alignment.Center
    ) {
        AndroidExternalSurface(
            modifier = Modifier,
            isSecure = true
        ) {
            onSurface { surface, _, _ ->
                setSurface(surface)
                surface.onDestroyed {
                    clearSurface()
                }
            }
        }
    }
}