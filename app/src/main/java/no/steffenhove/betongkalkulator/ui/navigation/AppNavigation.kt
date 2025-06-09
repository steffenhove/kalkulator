package no.steffenhove.betongkalkulator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import no.steffenhove.betongkalkulator.ui.screens.CalculationScreen
import no.steffenhove.betongkalkulator.ui.screens.VinkelfesteScreen
import no.steffenhove.betongkalkulator.ui.screens.HistoryScreen
import no.steffenhove.betongkalkulator.ui.screens.LoeftepunktScreen
import no.steffenhove.betongkalkulator.ui.screens.OverskjaeringScreen
import no.steffenhove.betongkalkulator.ui.screens.SettingsScreen
import no.steffenhove.betongkalkulator.ui.screens.StartScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "start") {
        composable("start") {
            StartScreen(navController)
        }
        composable("settings") {
            SettingsScreen(navController.context)
        }
        composable("calculator") {
            CalculationScreen(navController.context)
        }
        composable("history") {
            HistoryScreen(navController.context)
        }
        composable("festepunkt") {
            VinkelfesteScreen(navController.context)
        }
        composable("overskjaering") {
            OverskjaeringScreen(navController.context)
        }
        composable("loeftepunkt") {
            LoeftepunktScreen(viewModel())
        }
    }
}
