package no.steffenhove.betongkalkulator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.steffenhove.betongkalkulator.ui.screens.CalculationScreen
import no.steffenhove.betongkalkulator.ui.screens.SettingsScreen
import no.steffenhove.betongkalkulator.ui.screens.StartScreen
import no.steffenhove.betongkalkulator.ui.screens.HistoryScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("settings") { SettingsScreen() }
        composable("calculator") { CalculationScreen() }
        composable("history") { HistoryScreen() }
    }
}