package com.example.mediaplaybackapp.player.presentation.components

import android.view.Surface
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    setSurface: (Surface) -> Unit,
    clearSurface: () -> Unit,
) {
    AndroidExternalSurface(modifier = modifier) {
        onSurface { surface, _, _ ->
            setSurface(surface)
            surface.onDestroyed {
                clearSurface()
            }
        }
    }
}