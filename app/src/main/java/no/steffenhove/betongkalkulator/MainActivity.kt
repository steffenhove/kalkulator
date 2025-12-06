package no.steffenhove.betongkalkulator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import no.steffenhove.betongkalkulator.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Starter hele Compose-UI'et via AppNavigation
            AppNavigation()
        }
    }
}
