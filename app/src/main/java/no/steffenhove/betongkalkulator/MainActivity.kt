package no.steffenhove.betongkalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import no.steffenhove.betongkalkulator.ui.navigation.AppNavigation
import no.steffenhove.betongkalkulator.ui.theme.BetongKalkulatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BetongKalkulatorTheme {
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

@Composable
fun StartScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = { navController.navigate("calculator") }) {
            Text(text = "Kalkulator")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("settings") }) {
            Text(text = "Innstillinger")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("history") }) {
            Text(text = "Historikk")
        }
    }
}