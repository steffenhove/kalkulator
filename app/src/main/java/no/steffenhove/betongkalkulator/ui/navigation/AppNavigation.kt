package no.steffenhove.betongkalkulator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import no.steffenhove.betongkalkulator.ui.screens.CalculationScreen
import no.steffenhove.betongkalkulator.ui.screens.HistoryScreen
import no.steffenhove.betongkalkulator.ui.screens.LoeftepunktScreen
import no.steffenhove.betongkalkulator.ui.screens.OverskjaeringScreen
import no.steffenhove.betongkalkulator.ui.screens.SettingsScreen
import no.steffenhove.betongkalkulator.ui.screens.StartScreen
import no.steffenhove.betongkalkulator.ui.screens.VinkelfesteScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = "start"
    ) {

        // Startskjerm
        composable("start") {
            StartScreen(
                navigateToCalculation = { navController.navigate("calculation") },
                navigateToHistory = { navController.navigate("history") },
                navigateToSettings = { navController.navigate("settings") },
                navigateToVinkelfeste = { navController.navigate("vinkelfeste") },
                navigateToOverskjaering = { navController.navigate("overskjaering") },
                navigateToLoeftepunkt = { navController.navigate("loftepunkt") }
            )
        }

        // Beregning
        composable("calculation") {
            CalculationScreen(
                context = context,
                navigateBack = { navController.popBackStack() }
            )
        }

        // Historikk
        composable("history") {
            HistoryScreen(
                navigateBack = { navController.popBackStack() }
            )
        }

        // Innstillinger – ingen tilbakeknapp i denne versjonen
        composable("settings") {
            SettingsScreen()
        }

        // Overskjæring
        composable("overskjaering") {
            OverskjaeringScreen()
        }

        // Vinkelfeste
        composable("vinkelfeste") {
            VinkelfesteScreen(
                navigateBack = { navController.popBackStack() }
            )
        }

        // Løftepunkt
        composable("loftepunkt") {
            LoeftepunktScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}
