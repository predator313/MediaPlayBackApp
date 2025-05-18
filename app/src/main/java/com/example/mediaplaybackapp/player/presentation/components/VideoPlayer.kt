package com.example.mediaplaybackapp.player.presentation.components

import android.view.Surface
import androidx.annotation.DrawableRes
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.example.mediaplaybackapp.R
import com.example.mediaplaybackapp.player.domain.PlayerUiState

@Composable
fun VideoPlayer(
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
    setSurface: (Surface) -> Unit,
    clearSurface: () -> Unit,
    onExpendClick: () -> Unit,
    onCollapsedClick: () -> Unit,
    onVideoSurfaceClick: () -> Unit,
) {
    Box(
        modifier = modifier.aspectRatio(playerUiState.videoAspectRation)
            .clickable { onVideoSurfaceClick() },
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
        if (playerUiState.showPlayerControl) {
            VideoOverLay(
                modifier = Modifier.matchParentSize(),
                onCollapsedClick = onCollapsedClick,
                onExpendClick = onExpendClick,
                isInFullScreen = playerUiState.isFullScreen
            )
        }
    }
}

@Composable
fun VideoOverLay(
    isInFullScreen: Boolean,
    modifier: Modifier = Modifier,
    onCollapsedClick: () -> Unit,
    onExpendClick: () -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        PlaybackControl(
            modifier = Modifier.matchParentSize(),
            onCollapsedClick = onCollapsedClick,
            onExpendClick = onExpendClick,
            isInFullScreen = isInFullScreen,
        )
    }
}

@Composable
fun PlaybackControl(
    modifier: Modifier = Modifier,
    isInFullScreen: Boolean,
    onCollapsedClick: () -> Unit,
    onExpendClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            if (isInFullScreen) {
                PlayBackButton(
                    resId = R.drawable.ic_collapse_btn,
                    description = "exit full screen",
                    modifier = Modifier,
                    onClick = onCollapsedClick
                )
            } else {
                PlayBackButton(
                    resId = R.drawable.ic_expend_btn,
                    description = "enter full screen",
                    modifier = Modifier,
                    onClick = onExpendClick,
                )
            }
        }
    }

}

@Composable
fun PlayBackButton(
    @DrawableRes resId: Int,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Image(
        imageVector = ImageVector.vectorResource(id = resId),
        contentDescription = description,
        modifier = modifier
            .clickable { onClick() }
    )
}