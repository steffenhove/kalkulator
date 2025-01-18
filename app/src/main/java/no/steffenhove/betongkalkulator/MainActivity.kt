package no.steffenhove.betongkalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import no.steffenhove.betongkalkulator.ui.navigation.AppNavigation
import no.steffenhove.betongkalkulator.ui.theme.BetongKalkulatorTheme
import no.steffenhove.betongkalkulator.ui.viewmodel.CalculationViewModel
import no.steffenhove.betongkalkulator.ui.viewmodel.HistoryViewModel
import no.steffenhove.betongkalkulator.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val calculationViewModel: CalculationViewModel by viewModels()
        val historyViewModel: HistoryViewModel by viewModels()
        val settingsViewModel: SettingsViewModel by viewModels()

        setContent {
            BetongKalkulatorTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        calculationViewModel = calculationViewModel,
                        historyViewModel = historyViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}