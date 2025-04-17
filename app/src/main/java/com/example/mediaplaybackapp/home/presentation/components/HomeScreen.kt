package com.example.mediaplaybackapp.home.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mediaplaybackapp.R
import com.example.mediaplaybackapp.home.domain.HomeUiState

@Composable
fun HomeScreen(
    homeUiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(homeUiState.coverImageResourceId),
                contentDescription = "home banner",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.play_icon),
                contentDescription = "Play_pause",
                modifier = Modifier.size(40.dp)
            )
        }

        Text(
            text = homeUiState.attributionText,
            fontSize = 16.sp,
            fontStyle = FontStyle.Italic,
            color = Color.White,
        )
        Text(
            text = homeUiState.description,
            fontSize = 14.sp,
            color = Color.White,
        )
    }
}