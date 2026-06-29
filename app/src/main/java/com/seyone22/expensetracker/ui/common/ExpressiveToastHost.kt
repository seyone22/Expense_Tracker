package com.seyone22.expensetracker.ui.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.managers.ExpressiveToast
import com.seyone22.expensetracker.managers.SnackbarManager
import com.seyone22.expensetracker.managers.ToastType
import kotlinx.coroutines.delay

@Composable
fun ExpressiveToastHost(
    modifier: Modifier = Modifier
) {
    val activeToastState by SnackbarManager.activeToast.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        AnimatedVisibility(
            visible = activeToastState != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
        ) {
            activeToastState?.let { toast ->
                ExpressiveToastCard(toast = toast)
            }
        }
    }
}

@Composable
fun ExpressiveToastCard(toast: ExpressiveToast) {
    var progress by remember { mutableStateOf(1f) }

    LaunchedEffect(toast.id) {
        progress = 1f
        val steps = 100
        val delayTime = toast.durationMs / steps
        for (i in steps downTo 0) {
            delay(delayTime)
            progress = i.toFloat() / steps
        }
    }

    val containerColor = when (toast.type) {
        ToastType.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
        ToastType.ERROR -> MaterialTheme.colorScheme.errorContainer
        ToastType.WARNING -> MaterialTheme.colorScheme.tertiaryContainer
        ToastType.INFO -> MaterialTheme.colorScheme.secondaryContainer
    }

    val contentColor = when (toast.type) {
        ToastType.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
        ToastType.ERROR -> MaterialTheme.colorScheme.onErrorContainer
        ToastType.WARNING -> MaterialTheme.colorScheme.onTertiaryContainer
        ToastType.INFO -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    val icon = when (toast.type) {
        ToastType.SUCCESS -> Icons.Default.CheckCircle
        ToastType.ERROR -> Icons.Default.Warning
        ToastType.WARNING -> Icons.Default.Warning
        ToastType.INFO -> Icons.Default.Info
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .clip(RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(28.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = toast.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = toast.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.8f)
                    )
                }

                if (toast.actionLabel != null) {
                    TextButton(
                        onClick = {
                            toast.onAction?.invoke()
                            SnackbarManager.dismissToast()
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = contentColor
                        )
                    ) {
                        Text(
                            text = toast.actionLabel,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = contentColor,
                trackColor = contentColor.copy(alpha = 0.2f)
            )
        }
    }
}
