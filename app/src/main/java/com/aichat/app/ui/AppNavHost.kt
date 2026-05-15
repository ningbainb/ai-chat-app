package com.aichat.app.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aichat.app.ui.screens.HistoryScreen
import com.aichat.app.ui.screens.HomeScreen
import com.aichat.app.ui.screens.SettingsScreen
import com.aichat.app.viewmodel.MainViewModel

@Composable
fun AppNavHost(viewModel: MainViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(viewModel, navController) }
        composable("history") { HistoryScreen(viewModel, navController) }
        composable("settings") { SettingsScreen(viewModel, navController) }
    }
}
