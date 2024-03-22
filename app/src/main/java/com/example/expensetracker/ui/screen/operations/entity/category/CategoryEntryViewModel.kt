package com.example.expensetracker.ui.screen.operations.entity.category

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.expensetracker.data.category.CategoriesRepository
import com.example.expensetracker.model.Category

class CategoryEntryViewModel(private val categoriesRepository: CategoriesRepository) : ViewModel() {
}