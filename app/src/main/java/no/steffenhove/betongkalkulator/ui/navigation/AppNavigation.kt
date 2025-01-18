package no.steffenhove.betongkalkulator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.steffenhove.betongkalkulator.ui.screens.CalculationScreen
import no.steffenhove.betongkalkulator.ui.screens.HistoryScreen
import no.steffenhove.betongkalkulator.ui.screens.SettingsScreen
import no.steffenhove.betongkalkulator.ui.viewmodel.CalculationViewModel
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModel
import no.steffenhove.betongkalkulator.ui.viewmodel.SettingsViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    calculationViewModel: CalculationViewModel,
    historyViewModel: HistoryViewModel,
    settingsViewModel: SettingsViewModel
) {
    NavHost(navController = navController, startDestination = "calculation") {
        composable("calculation") {
            CalculationScreen(viewModel = calculationViewModel)
        }
        composable("history") {
            HistoryScreen(viewModel = historyViewModel)
        }
        composable("settings") {
            SettingsScreen(viewModel = settingsViewModel)
        }
    }
}