package com.example.mediaplaybackapp.player.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mediaplaybackapp.player.domain.AudioTrack
import com.example.mediaplaybackapp.player.domain.SubtitleTrack
import com.example.mediaplaybackapp.player.domain.TrackSelectionUiModel
import com.example.mediaplaybackapp.player.domain.VideoTrack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackSelector(
    trackSelectionUiModel: TrackSelectionUiModel,
    modifier: Modifier = Modifier,
    onVideoTrackSelected: (VideoTrack) -> Unit,
    onAudioTrackSelected: (AudioTrack) -> Unit,
    onSubtitleTrackSelected: (SubtitleTrack) -> Unit,
    onDismiss: () -> Unit,
) {
    var currentState by remember { mutableStateOf(TrackState.DEFAULT) }
    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = onDismiss
    ) {
        when (currentState) {
            TrackState.DEFAULT -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Video Tracks",
                        modifier = Modifier.clickable {
                            currentState = TrackState.VIDEO
                        }
                    )
                    Text(
                        text = "Audio Tracks",
                        modifier = Modifier.clickable {
                            currentState = TrackState.AUDIO
                        }
                    )
                    Text(
                        text = "Subtitle Tracks",
                        modifier = Modifier.clickable {
                            currentState = TrackState.SUBTITLE
                        }
                    )
                }
            }

            TrackState.VIDEO -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trackSelectionUiModel.videoTracks.forEach {
                        Text(
                            text = it.displayName,
                            modifier = Modifier.clickable {
                                onVideoTrackSelected(it)
                                onDismiss()
                            },
                            color = if (it == trackSelectionUiModel.selectedVideoTrack) Color.Yellow
                            else Color.White
                        )
                    }
                }
            }

            TrackState.AUDIO -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trackSelectionUiModel.audioTracks.forEach {
                        Text(
                            text = it.displayName,
                            modifier = Modifier.clickable {
                                onAudioTrackSelected(it)
                                onDismiss()
                            },
                            color = if (it == trackSelectionUiModel.selectedAudioTrack) Color.Yellow
                            else Color.White
                        )
                    }
                }
            }

            TrackState.SUBTITLE -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    trackSelectionUiModel.subtitleTracks.forEach {
                        Text(
                            text = it.displayName,
                            modifier = Modifier.clickable {
                                onSubtitleTrackSelected(it)
                                onDismiss()
                            },
                            color = if (it == trackSelectionUiModel.selectedSubtitleTrack) Color.Yellow
                            else Color.White
                        )
                    }
                }
            }
        }
    }
}

private enum class TrackState {
    DEFAULT,
    VIDEO,
    AUDIO,
    SUBTITLE
}