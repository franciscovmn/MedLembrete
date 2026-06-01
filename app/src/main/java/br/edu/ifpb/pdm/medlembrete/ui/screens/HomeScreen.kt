package br.edu.ifpb.pdm.medlembrete.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.pdm.medlembrete.ui.components.MedicamentoCard
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.HomeUiState
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.HomeViewModel
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.ItemHome

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAbrirDetalhe: (medicamentoId: String) -> Unit,
    onAbrirHistorico: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val titulo = when (state) {
        is HomeUiState.Success -> "Hoje · ${(state as HomeUiState.Success).nomePaciente}"
        else -> "Home"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo) },
                actions = {
                    IconButton(onClick = onAbrirHistorico) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Histórico"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val s = state) {
            is HomeUiState.Loading -> CarregandoConteudo(innerPadding)
            is HomeUiState.Error -> ErroConteudo(
                mensagem = s.mensagem,
                onTentarNovamente = viewModel::recarregar,
                paddingValues = innerPadding
            )
            is HomeUiState.Success -> SucessoConteudo(
                pendentes = s.pendentes,
                tomados = s.tomados,
                onAbrirDetalhe = onAbrirDetalhe,
                onConfirmar = viewModel::confirmarMedicacao,
                paddingValues = innerPadding
            )
        }
    }
}

@Composable
private fun CarregandoConteudo(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErroConteudo(
    mensagem: String,
    onTentarNovamente: () -> Unit,
    paddingValues: PaddingValues
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Algo deu errado",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = onTentarNovamente) {
            Text("Tentar novamente")
        }
    }
}

@Composable
private fun SucessoConteudo(
    pendentes: List<ItemHome>,
    tomados: List<ItemHome>,
    onAbrirDetalhe: (medicamentoId: String) -> Unit,
    onConfirmar: (ItemHome) -> Unit,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Pendentes (${pendentes.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }
        if (pendentes.isEmpty()) {
            item { TextoVazio("Nenhum medicamento pendente hoje") }
        } else {
            items(
                items = pendentes,
                key = { "pend-${it.medicamento.id}-${it.horario.id}" }
            ) { item ->
                MedicamentoCard(
                    item = item,
                    onConfirmar = { onConfirmar(item) },
                    onClick = { item.medicamento.id?.let(onAbrirDetalhe) }
                )
            }
        }

        item { Spacer(Modifier.height(8.dp)) }

        item {
            Text(
                text = "Tomados (${tomados.size})",
                style = MaterialTheme.typography.titleLarge
            )
        }
        if (tomados.isEmpty()) {
            item { TextoVazio("Nenhum medicamento tomado hoje") }
        } else {
            items(
                items = tomados,
                key = { "tom-${it.medicamento.id}-${it.horario.id}-${it.registro?.id}" }
            ) { item ->
                MedicamentoCard(
                    item = item,
                    onConfirmar = { onConfirmar(item) },
                    onClick = { item.medicamento.id?.let(onAbrirDetalhe) }
                )
            }
        }
    }
}

@Composable
private fun TextoVazio(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}
