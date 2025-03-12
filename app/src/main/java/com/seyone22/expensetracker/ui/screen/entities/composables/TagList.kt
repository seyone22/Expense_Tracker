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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.seyone22.expensetracker.SharedViewModel
import com.seyone22.expensetracker.ui.AppViewModelProvider
import com.seyone22.expensetracker.ui.common.ProfileAvatarWithFallback
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import kotlinx.coroutines.CoroutineScope
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TagList(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope,
    viewModel: EntityViewModel
) {
    val haptics = LocalHapticFeedback.current

    val sharedViewModel: SharedViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val tagsList by sharedViewModel.tagsFlow.collectAsState(initial = listOf())
    val list = tagsList.sortedBy { t -> t.tagName[0].lowercaseChar() }


    LazyColumn {
        items(list, key = { it.tagId }) {

            ListItem(headlineContent = { Text(it.tagName) },
                leadingContent = {
                    ProfileAvatarWithFallback(
                        size = 44.dp,
                        fontSize = 22.sp,
                        initial = it.tagName[0].toString().uppercase(
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
                }, onLongClickLabel = "  "
                )
            )
            HorizontalDivider()

        }
    }
}