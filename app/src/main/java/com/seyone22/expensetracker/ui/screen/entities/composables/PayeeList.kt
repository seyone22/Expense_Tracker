package com.seyone22.expensetracker.ui.screen.entities.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.seyone22.expensetracker.data.model.Payee
import com.seyone22.expensetracker.ui.common.ProfileAvatarWithFallback
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import kotlinx.coroutines.CoroutineScope
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PayeeList(
    modifier: Modifier = Modifier,
    list: List<Payee>,
    coroutineScope: CoroutineScope,
    longClicked: (Payee) -> Unit,
    viewModel: EntityViewModel
) {
    val haptics = LocalHapticFeedback.current

    LazyColumn {
        items(list, key = { it.payeeId }) {

            ListItem(headlineContent = { Text(it.payeeName) },
                overlineContent = { Text(it.payeeId.toString()) },
                leadingContent = {
                    ProfileAvatarWithFallback(
                        size = 44.dp,
                        fontSize = 22.sp,
                        initial = it.payeeName[0].toString().uppercase(
                            Locale.ROOT
                        ),
                    )
                },
                trailingContent = {
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null
                    )
                },
                modifier = Modifier.combinedClickable(onClick = {}, onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    longClicked(it)
                }, onLongClickLabel = "  "
                )
            )
            HorizontalDivider()

        }
    }
}