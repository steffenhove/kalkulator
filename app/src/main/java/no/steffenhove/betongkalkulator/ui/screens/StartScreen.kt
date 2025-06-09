package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun StartScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MenuButton(
                text = "Kalkulator",
                icon = Icons.Filled.Calculate,
                onClick = { navController.navigate("calculator") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "Overskjæring",
                icon = Icons.Filled.Build,
                onClick = { navController.navigate("overskjaering") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "Festepunkt vinkelboring",
                icon = Icons.Filled.Build,
                onClick = { navController.navigate("festepunkt") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "Løftepunkt",
                icon = Icons.Filled.Build,
                onClick = { navController.navigate("loeftepunkt") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(
                text = "Historikk",
                icon = Icons.Filled.History,
                onClick = { navController.navigate("history") }
            )
        }

        MenuButton(
            text = "Innstillinger",
            icon = Icons.Filled.Settings,
            onClick = { navController.navigate("settings") }
        )
    }
}

@Composable
fun MenuButton(text: String, icon: ImageVector, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = text)
        Spacer(modifier = Modifier.width(8.dp))
        Text(text)
    }
}
