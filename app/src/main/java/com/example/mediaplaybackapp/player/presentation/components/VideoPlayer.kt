package com.example.mediaplaybackapp.player.presentation.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.DefaultTimeBar
import androidx.media3.ui.TimeBar
import com.example.mediaplaybackapp.R
import com.example.mediaplaybackapp.player.domain.PlaybackState
import com.example.mediaplaybackapp.player.domain.PlayerUiState
import com.example.mediaplaybackapp.player.presentation.action.PlayerAction
import com.example.mediaplaybackapp.utils.formatMsToString
import com.example.mediaplaybackapp.utils.isReady
import timber.log.Timber

@Composable
fun VideoPlayer(
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
    onExpendClick: () -> Unit,
    onCollapsedClick: () -> Unit,
    onVideoSurfaceClick: () -> Unit,
    onPlayerAction: (PlayerAction) -> Unit,
) {
    Box(
        modifier = modifier
            .aspectRatio(playerUiState.videoAspectRation)
            .clickable { onVideoSurfaceClick() },
        contentAlignment = Alignment.Center
    ) {
        AndroidExternalSurface(
            modifier = Modifier,
            isSecure = true
        ) {
            onSurface { surface, _, _ ->
                onPlayerAction(PlayerAction.AttachSurface(surface = surface))
                surface.onDestroyed {
                    onPlayerAction(PlayerAction.DetachSurface)
                }
            }
        }
        if (playerUiState.showPlayerControl) {
            VideoOverLay(
                modifier = Modifier.matchParentSize(),
                onCollapsedClick = onCollapsedClick,
                onExpendClick = onExpendClick,
                playerUiState = playerUiState,
                onPlayerAction = onPlayerAction
            )
        }
    }
}

@Composable
fun VideoOverLay(
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
    onCollapsedClick: () -> Unit,
    onExpendClick: () -> Unit,
    onPlayerAction: (PlayerAction) -> Unit,
) {
    Box(
        modifier = modifier
    ) {
        PlaybackControl(
            modifier = Modifier.matchParentSize(),
            onCollapsedClick = onCollapsedClick,
            onExpendClick = onExpendClick,
            playerUiState = playerUiState,
            onPlayerAction = onPlayerAction,
        )
    }
}

@Composable
fun PlaybackControl(
    playerUiState: PlayerUiState,
    modifier: Modifier = Modifier,
    onCollapsedClick: () -> Unit,
    onExpendClick: () -> Unit,
    onPlayerAction: (PlayerAction) -> Unit,
) {
    Box(
        modifier = modifier
            .background(color = Color(0xA0000000))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            if (playerUiState.isFullScreen) {
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
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (playerUiState.playbackState.isReady()) {
                PlayBackButton(
                    resId = R.drawable.fast_rewind_btn,
                    description = "rewind btn",
                    onClick = {
                        onPlayerAction(PlayerAction.Start(0L))
                    }
                )

                PlayBackButton(
                    resId = R.drawable.ic_skip_prev,
                    description = "start over",
                    onClick = {
                        onPlayerAction(PlayerAction.Start(10_000L))
                    }
                )
            }

            when (playerUiState.playbackState) {
                PlaybackState.IDLE -> {
                    PlayBackButton(
                        resId = R.drawable.play_icon,
                        description = "play",
                        onClick = {
                            onPlayerAction(PlayerAction.Start())
                        }
                    )
                }

                PlaybackState.PLAYING -> {
                    PlayBackButton(
                        resId = R.drawable.ic_pause_btn,
                        description = "pause",
                        onClick = {
                            onPlayerAction(PlayerAction.Pause)
                        }
                    )
                }

                PlaybackState.PAUSED -> {
                    PlayBackButton(
                        resId = R.drawable.play_icon,
                        description = "play",
                        onClick = {
                            onPlayerAction(PlayerAction.Resume)
                        }
                    )
                }

                PlaybackState.BUFFERING -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White
                    )
                }

                PlaybackState.COMPLETED -> {
                    PlayBackButton(
                        resId = R.drawable.ic_replay_btn,
                        description = "reload",
                        onClick = {
                            onPlayerAction(PlayerAction.Start(0))
                        }
                    )
                }

                PlaybackState.ERROR -> {
                    PlayBackButton(
                        resId = R.drawable.ic_play_back_error_btn,
                        description = "error",
                    )
                    PlayBackButton(
                        resId = R.drawable.ic_replay_btn,
                        description = "reload",
                        onClick = {
                            //todo letter we will fix it
                            onPlayerAction(PlayerAction.Start(0))
                        }
                    )
                }
            }
            if (playerUiState.playbackState.isReady()) {
                PlayBackButton(
                    resId = R.drawable.fast_forward_btn,
                    description = "fast forward",
                    onClick = {}
                )
            }
        }
        playerUiState.timeLineUiModel?.let { timeLineUiModel ->
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                PlaybackPosition(
                    contentDurationInMs = timeLineUiModel.durationsInMs,
                    contentPositionInMs = timeLineUiModel.currentPositionInMs
                )
                TimeBar(
                    positionInMs = timeLineUiModel.currentPositionInMs,
                    durationInMs = timeLineUiModel.durationsInMs,
                    bufferedPositionInMs = timeLineUiModel.bufferedPositionInMs,
                    modifier = Modifier,
                    onSeek = {
                        onPlayerAction(PlayerAction.Seek(it.toLong()))
                    }
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
    onClick: () -> Unit = {},
) {
    Image(
        imageVector = ImageVector.vectorResource(id = resId),
        contentDescription = description,
        modifier = modifier
            .clickable { onClick() }
    )
}

@Composable
fun PlaybackPosition(
    contentPositionInMs: Long,
    contentDurationInMs: Long,
) {
    val formattedContentDur = formatMsToString(contentDurationInMs)
    val formattedContentPos = formatMsToString(contentPositionInMs)
    Text(
        text = "$formattedContentPos / $formattedContentDur",
        fontSize = 10.sp
    )
}

@Composable
fun TimeBar(
    positionInMs: Long,
    durationInMs: Long,
    bufferedPositionInMs: Long,
    modifier: Modifier = Modifier,
    onSeek: (Float) -> Unit,
) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        factory = { context ->
            DefaultTimeBar(
                context
            ).apply {
                setScrubberColor(0xFFFF0000.toInt())
                setPlayedColor(0xCCFF0000.toInt())
                setBufferedColor(0x77FF0000)
            }
        },
        update = { timeBar ->
            with(timeBar) {
                addListener(object : TimeBar.OnScrubListener {
                    override fun onScrubStart(
                        timeBar: TimeBar,
                        position: Long
                    ) {
                    }

                    override fun onScrubMove(
                        timeBar: TimeBar,
                        position: Long
                    ) {
                    }

                    override fun onScrubStop(
                        timeBar: TimeBar,
                        position: Long,
                        canceled: Boolean
                    ) {
                        onSeek(position.toFloat())
                    }
                }
                )
                setDuration(durationInMs)
                setPosition(positionInMs)
                setBufferedPosition(bufferedPositionInMs)
            }
        }
    )
}