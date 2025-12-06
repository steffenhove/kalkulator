package no.steffenhove.betongkalkulator.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.steffenhove.betongkalkulator.ui.components.AppScaffold

@Composable
fun StartScreen(
    navigateToCalculation: () -> Unit,
    navigateToHistory: () -> Unit,
    navigateToSettings: () -> Unit,
    navigateToVinkelfeste: () -> Unit,
    navigateToOverskjaering: () -> Unit,
    navigateToLoeftepunkt: () -> Unit
) {
    AppScaffold(
        title = "Betongsaging"
        // ingen navigateBack => ingen tilbakeknapp på startsiden
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Øverst – tittel / intro
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Velg funksjon:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.padding(top = 8.dp))
            }

            // Midtdel – hovedfunksjoner + verktøy
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // HOVEDFUNKSJONER
                Text(
                    text = "Hovedfunksjoner",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 4.dp)
                )

                Button(
                    onClick = navigateToCalculation,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Beregning (vekt / volum)")
                }

                Spacer(modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = navigateToHistory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Historikk")
                }

                Spacer(modifier = Modifier.padding(top = 24.dp))

                // VERKTØY
                Text(
                    text = "Verktøy",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 4.dp)
                )

                Button(
                    onClick = navigateToOverskjaering,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Overskjæring")
                }

                Spacer(modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = navigateToVinkelfeste,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Vinkelboring / feste for stativ")
                }

                Spacer(modifier = Modifier.padding(top = 8.dp))

                Button(
                    onClick = navigateToLoeftepunkt,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Løftepunkt")
                }
            }

            // Nederst – innstillinger
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.padding(top = 8.dp))
                Button(
                    onClick = navigateToSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Innstillinger")
                }
            }
        }
    }
}
