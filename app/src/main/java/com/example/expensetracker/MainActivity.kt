package com.example.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.expensetracker.data.MMEXDatabase
import com.example.expensetracker.ui.theme.ExpenseTrackerTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?)
    {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            ExpenseTrackerTheme {
                // A surface container using the 'background' color from the theme
                Surface( modifier = Modifier.fillMaxSize() )
                {
                    ExpenseApp()
                }
            }
        }
    }

    private suspend fun isUsed() : Boolean {
        val metadataDao = MMEXDatabase.getDatabase(this).metadataDao()
        var isUsed = false

        lifecycleScope.launch {
            metadataDao.getMetadataByName("ISUSED").collect { metadata ->
                isUsed = metadata?.infoValue == "TRUE"
            }
        }

        return isUsed
    }
}



@Preview(showBackground = true)
@Composable
fun LayoutPreview() {
    ExpenseTrackerTheme {
        ExpenseApp()
    }
}