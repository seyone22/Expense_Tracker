package com.seyone22.expensetracker.ui.screen.entities.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.seyone22.expensetracker.data.model.Category
import com.seyone22.expensetracker.ui.common.removeTrPrefix
import com.seyone22.expensetracker.ui.screen.entities.EntityViewModel
import com.seyone22.expensetracker.utils.TransactionCategoryIcons
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CategoryList(
    modifier: Modifier = Modifier,
    listParent: List<Category>,
    listSub: List<Category>,
    viewModel: EntityViewModel,
    longClicked: (Category) -> Unit,
    coroutineScope: CoroutineScope,
    onClicked: (Int) -> Unit = {},
    isSelected: Boolean,
) {
    val haptics = LocalHapticFeedback.current
    val groupedList = (listSub.groupBy { it.parentId })
    val selectedEntity by viewModel.selectedEntity.collectAsState()

    LazyColumn {
        item {
            listParent.forEach { parent ->
                ListItem(headlineContent = { Text(removeTrPrefix(parent.categName)) },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Bookmark,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.combinedClickable(onClick = {
                        viewModel.setSelectedEntity(parent)
                        onClicked(parent.categId)
                    }, onLongClick = {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        longClicked(parent)
                    }, onLongClickLabel = "  "
                    ),
                    tonalElevation = if (selectedEntity == parent && isSelected) 200.dp else 0.dp
                )


                groupedList.forEach { group ->
                    if (group.key == parent.categId) {
                        group.value.forEach {
                            ListItem(headlineContent = { Text(removeTrPrefix(it.categName)) },
                                leadingContent = {
                                    Icon(
                                        TransactionCategoryIcons.getIconForCategory(it.categName),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                },
                                modifier = Modifier
                                    .combinedClickable(
                                        onClick = {
                                            viewModel.setSelectedEntity(it)
                                            onClicked(it.categId)
                                        }, onLongClick = {
                                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            longClicked(it)
                                        }, onLongClickLabel = "  "
                                    )
                                    .padding(24.dp, 0.dp, 0.dp, 0.dp),
                                tonalElevation = if (selectedEntity == it && isSelected) 200.dp else 0.dp
                            )
                        }
                    }
                }
            }
        }
    }
}