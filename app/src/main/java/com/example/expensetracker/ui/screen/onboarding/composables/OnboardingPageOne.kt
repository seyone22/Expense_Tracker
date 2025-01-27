package com.example.expensetracker.ui.screen.onboarding.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.expensetracker.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingPageOne(
    pageIndex: Int = 1, setButtonState: (Boolean) -> Unit
) {
    val onPrimary = MaterialTheme.colorScheme.inversePrimary
    val primary = MaterialTheme.colorScheme.primaryContainer
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiaryContainer
    val background = MaterialTheme.colorScheme.background

    // Initially set the button state to false
    LaunchedEffect(Unit) {
        // Enable button when both fields are filled
        setButtonState(true)
    }

    Box(
        modifier = Modifier
    ) {
        // Background canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            clipRect {
                drawCircle(
                    color = onPrimary, radius = 240f, center = Offset(200F, 900F)
                )
                drawRoundRect(
                    color = secondary,
                    cornerRadius = CornerRadius(1000F),
                    size = Size(1100F, 380F),
                    topLeft = Offset(size.width / 5.4f, (size.height / 10f))
                )
                drawRoundRect(
                    color = secondary,
                    cornerRadius = CornerRadius(1000F),
                    size = Size(380F, 1000F),
                    topLeft = Offset(size.width / 1.4f, (size.height / 3f))
                )
                drawRoundRect(
                    color = background,
                    cornerRadius = CornerRadius(1000F),
                    size = Size(320F, 600F),
                    topLeft = Offset(size.width / 1.35f, (size.height / 2.87f))
                )
                drawCircle(
                    color = primary,
                    radius = 240f,
                    center = Offset(size.width / 1.3f, size.height / 4f)
                )
            }
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(R.drawable.wavy_circle).build(),
            colorFilter = ColorFilter.tint(tertiary),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )

        // Place the icon
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(150.dp) // Icon size relative to the circle
                .padding(start = 50.dp, top = 50.dp) // Padding to keep it well within the circle
        )

        // Text on top of the canvas
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .width(310.dp)
                .padding(16.dp)
        ) {
            Text(
                text = "Take control of your finances",
                style = MaterialTheme.typography.displayLarge,
            )
            Text(
                text = "This app will help you understand, control, and track your spending through all your different accounts.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 32.dp)
            )
        }
    }
}