package com.seyone22.expensetracker.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileAvatarWithFallback(
    image: Painter? = null,
    initial: String = "S",
    size: Dp = 64.dp,
    fontSize: TextUnit = 28.sp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        image?.let {
            Image(
                painter = it,
                contentDescription = "Profile Picture",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: Text(
            text = initial.uppercase(),
            fontSize = fontSize,
            color = Color.White
        )
    }
}
