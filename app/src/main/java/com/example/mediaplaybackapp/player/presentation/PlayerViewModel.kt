package com.example.mediaplaybackapp.player.presentation

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediaplaybackapp.R
import com.example.mediaplaybackapp.player.data.ExoplayerTrack
import com.example.mediaplaybackapp.player.domain.AudioTrack
import com.example.mediaplaybackapp.player.domain.PlaybackState
import com.example.mediaplaybackapp.player.domain.PlayerUiState
import com.example.mediaplaybackapp.player.domain.SubtitleTrack
import com.example.mediaplaybackapp.player.domain.TimeLineUiModel
import com.example.mediaplaybackapp.player.domain.TrackSelectionUiModel
import com.example.mediaplaybackapp.player.domain.VideoTrack
import com.example.mediaplaybackapp.player.presentation.action.PlayerAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer
) : ViewModel() {
    private val _playerUiStateFlow = MutableStateFlow(PlayerUiState())
    val playerUiStateFlow = _playerUiStateFlow.asStateFlow()

    private val playerCoroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private var positionTrackingJob: Job? = null

    private var selectedVideoTrack: VideoTrack = VideoTrack.AUTO
    private var selectedAudioTrack: AudioTrack = AudioTrack.AUTO
    private var selectedSubtitleTrack: SubtitleTrack = SubtitleTrack.AUTO

    private var videoTrackMap: Map<VideoTrack, ExoplayerTrack?> = emptyMap()
    private var audioTrackMap: Map<AudioTrack, ExoplayerTrack?> = emptyMap()
    private var subtitleTrackMap: Map<SubtitleTrack, ExoplayerTrack?> = emptyMap()


    private val playerEventListener: Player.Listener = object : Player.Listener {
        override fun onVideoSizeChanged(videoSize: VideoSize) {
            super.onVideoSizeChanged(videoSize)
            if (videoSize != VideoSize.UNKNOWN) {
                val videoWidth = videoSize.width
                val videoHeight = videoSize.height / videoSize.pixelWidthHeightRatio
                val videoAspectRatio = videoWidth / videoHeight
                _playerUiStateFlow.update {
                    it.copy(videoAspectRation = videoAspectRatio)
                }
            }

        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            if (isPlaying) {
                _playerUiStateFlow.update {
                    it.copy(
                        playbackState = PlaybackState.PLAYING
                    )
                }
            } else if (exoPlayer.playbackState == Player.STATE_READY) {
                _playerUiStateFlow.update {
                    it.copy(
                        playbackState = PlaybackState.PAUSED
                    )
                }
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            val state = when (playbackState) {
                Player.STATE_IDLE -> {
                    if (exoPlayer.playerError != null) {
                        PlaybackState.ERROR
                    } else {
                        PlaybackState.IDLE
                    }
                }

                Player.STATE_BUFFERING -> {
                    PlaybackState.BUFFERING
                }

                Player.STATE_READY -> {
                    if (exoPlayer.playWhenReady) {
                        PlaybackState.PLAYING
                    } else {
                        PlaybackState.PAUSED
                    }
                }

                Player.STATE_ENDED -> {
                    PlaybackState.COMPLETED
                }

                else -> {
                    PlaybackState.IDLE
                }
            }
            _playerUiStateFlow.update {
                it.copy(playbackState = state)
            }

            if (state == PlaybackState.ERROR) showPlayerControl()

            when (playbackState) {
                Player.STATE_READY -> {
                    startTrackingPlaybackPosition()
                }

                else -> stopTrackingPlaybackPosition()
            }

            when (playbackState) {
                Player.STATE_READY -> hidePlaceholderImage()
                Player.STATE_IDLE,
                Player.STATE_ENDED -> {
                    showPlaceholderImage()
                }

                else -> {}
            }

        }

        override fun onTracksChanged(tracks: Tracks) {
            super.onTracksChanged(tracks)
            val newVideoTracks = mutableMapOf<VideoTrack, ExoplayerTrack?>(
                VideoTrack.AUTO to null
            )

            val newAudioTracks = mutableMapOf<AudioTrack, ExoplayerTrack?>(
                AudioTrack.AUTO to null,
                AudioTrack.NONE to null
            )

            val newSubtitleTracks = mutableMapOf<SubtitleTrack, ExoplayerTrack?>(
                SubtitleTrack.AUTO to null,
                SubtitleTrack.NONE to null
            )
            tracks.groups.forEach { tracksGroup ->
                when (tracksGroup.type) {
                    C.TRACK_TYPE_AUDIO -> newAudioTracks.putAll(extractAudioTracks(tracksGroup))
                    C.TRACK_TYPE_VIDEO -> newVideoTracks.putAll(extractVideoTracks(tracksGroup))
                    C.TRACK_TYPE_TEXT -> newSubtitleTracks.putAll(extractSubtitleTracks(tracksGroup))
                }
            }
            videoTrackMap = newVideoTracks
            audioTrackMap = newAudioTracks
            subtitleTrackMap = newSubtitleTracks
            _playerUiStateFlow.update {
                it.copy(
                    trackSelection = TrackSelectionUiModel(
                        selectedVideoTrack = selectedVideoTrack,
                        videoTracks = videoTrackMap.keys.toList(),
                        selectedAudioTrack = selectedAudioTrack,
                        audioTracks = audioTrackMap.keys.toList(),
                        selectedSubtitleTrack = selectedSubtitleTrack,
                        subtitleTracks = subtitleTrackMap.keys.toList()
                    )
                )
            }
        }
    }

    init {
        exoPlayer.addListener(playerEventListener)
    }

    fun enterFullScreen() {
        _playerUiStateFlow.update {
            it.copy(
                isFullScreen = true,
                showPlayerControl = false
            )
        }
    }

    fun exitFullScreen() {
        _playerUiStateFlow.update {
            it.copy(
                isFullScreen = false,
                showPlayerControl = false
            )
        }
    }

    fun showPlayerControl() {
        _playerUiStateFlow.update {
            if (it.showPlayerControl) {
                it.copy(showPlayerControl = false)
            } else it.copy(showPlayerControl = true)
        }
    }

    fun handlePlayerAction(action: PlayerAction) {
        when (action) {
            is PlayerAction.AttachSurface -> {
                exoPlayer.setVideoSurface(action.surface)
            }

            is PlayerAction.DetachSurface -> {
                exoPlayer.setVideoSurface(null)
            }

            is PlayerAction.FastForward -> {
                exoPlayer.seekTo(exoPlayer.currentPosition + action.amountInMs)
            }

            is PlayerAction.Init -> {
                val mediaItem = MediaItem.Builder().apply {
                    setUri(action.streamUrl.toUri())
                }
                exoPlayer.setMediaItem(mediaItem.build())
            }

            is PlayerAction.Pause -> {
                exoPlayer.pause()
            }

            is PlayerAction.Resume -> {
                exoPlayer.play()
            }

            is PlayerAction.Rewind -> {
                exoPlayer.seekTo(exoPlayer.currentPosition - action.amountInMs)
            }

            is PlayerAction.Seek -> {
                exoPlayer.seekTo(action.amountInMs)
            }

            is PlayerAction.Start -> {
                exoPlayer.apply {
                    prepare()
                    play()
                    action.positionInMs?.let {
                        seekTo(it)
                    }
                }
            }

            is PlayerAction.Stop -> {
                exoPlayer.stop()
            }
        }
    }

    private fun startTrackingPlaybackPosition() {
        positionTrackingJob = playerCoroutineScope.launch {
            while (true) {
                val newTimeLineUiModel = buildTimeLineUiModel()
                _playerUiStateFlow.update {
                    it.copy(
                        timeLineUiModel = newTimeLineUiModel
                    )
                }
                delay(1_000)
            }
        }
    }

    private fun stopTrackingPlaybackPosition() {
        buildTimeLineUiModel(
        )
        positionTrackingJob?.cancel()
        positionTrackingJob = null
    }

    private fun buildTimeLineUiModel(): TimeLineUiModel? {
        val duration = exoPlayer.contentDuration
        if (duration == C.TIME_UNSET) return null
        val currentPosition = exoPlayer.contentPosition
        val bufferedPosition = exoPlayer.contentBufferedPosition
        return TimeLineUiModel(
            durationsInMs = duration,
            currentPositionInMs = currentPosition,
            bufferedPositionInMs = bufferedPosition
        )
    }

    private fun showPlaceholderImage() {
        _playerUiStateFlow.update {
            it.copy(showPlaceholderImg = R.drawable.home_icon)
        }
    }

    private fun hidePlaceholderImage() {
        _playerUiStateFlow.update {
            it.copy(showPlaceholderImg = null)
        }
    }

    private fun extractAudioTracks(info: Tracks.Group): Map<AudioTrack, ExoplayerTrack> {
        val result = mutableMapOf<AudioTrack, ExoplayerTrack>()
        for (trackIndex in 0 until info.mediaTrackGroup.length) {
            if (info.isTrackSupported(trackIndex)) {
                val format = info.mediaTrackGroup.getFormat(trackIndex)
                val language = format.language
                if (language != null) {
                    val audioTrack = AudioTrack(language = language)
                    result[audioTrack] = ExoplayerTrack(
                        trackGroup = info.mediaTrackGroup,
                        trackIndexInGroup = trackIndex
                    )
                }
            }
        }
        return result
    }

    private fun extractVideoTracks(info: Tracks.Group): Map<VideoTrack, ExoplayerTrack> {
        val result = mutableMapOf<VideoTrack, ExoplayerTrack>()
        for (trackIndex in 0 until info.mediaTrackGroup.length) {
            if (info.isTrackSupported(trackIndex)) {
                val format = info.mediaTrackGroup.getFormat(trackIndex)
                val videoTrack = VideoTrack(width = format.width, height = format.height)
                result[videoTrack] = ExoplayerTrack(
                    trackGroup = info.mediaTrackGroup,
                    trackIndexInGroup = trackIndex
                )
            }
        }
        return result
    }

    private fun extractSubtitleTracks(info: Tracks.Group): Map<SubtitleTrack, ExoplayerTrack> {
        val result = mutableMapOf<SubtitleTrack, ExoplayerTrack>()
        for (trackIndex in 0 until info.mediaTrackGroup.length) {
            if (info.isTrackSupported(trackIndex)) {
                val format = info.mediaTrackGroup.getFormat(trackIndex)
                val language = format.language
                if (language != null) {
                    val subtitleTrack = SubtitleTrack(language = language)
                    result[subtitleTrack] = ExoplayerTrack(
                        trackGroup = info.mediaTrackGroup,
                        trackIndexInGroup = trackIndex
                    )
                }
            }
        }
        return result
    }
}