package br.edu.ifpb.pdm.medlembrete.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAbrirDetalhe: (medicamentoId: String) -> Unit,
    onAbrirHistorico: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Home") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Tela Home — em construção")

            Button(
                onClick = { onAbrirDetalhe("med1") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver detalhe (teste)")
            }

            Button(
                onClick = onAbrirHistorico,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver histórico")
            }
        }
    }
}
