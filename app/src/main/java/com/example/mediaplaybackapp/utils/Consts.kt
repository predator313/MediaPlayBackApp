package com.example.mediaplaybackapp.utils

import android.content.Context
import android.content.Intent
import com.example.mediaplaybackapp.player.presentation.PlayerActivity

const val STREAM_URL_KEY = "stream_url_key"
fun navigateViaIntent(
    context: Context,
    streamUrl: String,
): Intent{
    val intent = Intent(
         context,
        PlayerActivity::class.java
    )
    return intent.apply {
        putExtra(STREAM_URL_KEY, streamUrl)
    }
}