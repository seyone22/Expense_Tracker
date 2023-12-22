package com.example.expensetracker.ui.common

import android.graphics.Typeface
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun AnimatedBar(
    values: List<Double>,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    val height = with(LocalDensity.current) { (200.dp.toPx()) }

    val proportions = getProportion(values, height)

    Canvas(modifier) {
        var spacer = 0
        proportions.forEachIndexed { index, value ->
            drawRect(
                color = Color.Blue,
                topLeft = Offset(x = 50f + spacer, y = (height - value)),
                size = Size(width = 100f, height = value),
                )
            spacer += 110
        }
        drawLine(
            color = Color.Black,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = with(density) { 1.dp.toPx() }
        )
    }
}

private fun getProportion (values : List<Double>, height : Float) : List<Float> {
    return values.map {
        ((it / values.sum())*(height * 2)).toFloat()
    }
}