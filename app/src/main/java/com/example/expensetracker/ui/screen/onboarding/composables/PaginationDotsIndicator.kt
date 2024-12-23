import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PaginationDotsIndicator(
    totalDots: Int,
    selectedIndex: Int,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (pip in 0 until totalDots) {
            val width by animateDpAsState(
                targetValue = if (pip == selectedIndex) 24.dp else 16.dp, label = ""
            )
            val color by animateColorAsState(
                targetValue = if (pip == selectedIndex) selectedColor else Color.Gray.copy(alpha = 0.4f),
                label = ""
            )
            Canvas(
                modifier = Modifier
                    .height(16.dp)
                    .width(width) // Active dot is larger
                    .padding(4.dp)
            ) {
                drawRoundRect(
                    color = color,
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx(), 8.dp.toPx()) // Rounded corners for pill shape
                )
            }
        }
    }
}
