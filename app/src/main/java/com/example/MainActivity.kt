package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.TransactionRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.FinanceViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Initialize Database & Repository cleanly
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = TransactionRepository(
      database.transactionDao,
      database.savingDao,
      database.categoryDao
    )

    setContent {
      MyApplicationTheme {
        // Instantiate the ViewModel cleanly with our factory
        val viewModel: FinanceViewModel = viewModel(
          factory = FinanceViewModelFactory(repository)
        )
        
        // Initialize programmatically-saved Firebase App and check active Session
        viewModel.initializeFirebaseOnStart(applicationContext)
        
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          // Android Scaffold manages edge-to-edge padding
          // Our DashboardScreen has its own scaffolding and handles its internal container padding nicely
          DashboardScreen(viewModel = viewModel)
        }
      }
    }
  }
}
