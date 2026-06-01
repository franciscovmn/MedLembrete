package br.edu.ifpb.pdm.medlembrete.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.pdm.medlembrete.enums.StatusMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Usuario
import br.edu.ifpb.pdm.medlembrete.ui.util.toDataFormatada
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.FiltrosState
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.HistoricoUiState
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.HistoricoViewModel
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.ItemHistorico

private val VerdeTomado = Color(0xFF2E7D32)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoricoScreen(
    onVoltar: () -> Unit,
    viewModel: HistoricoViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filtros by viewModel.filtros.collectAsStateWithLifecycle()
    val usuariosDisponiveis by viewModel.usuariosDisponiveis.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.carregarHistorico()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SecaoFiltros(
                filtros = filtros,
                usuariosDisponiveis = usuariosDisponiveis,
                onBuscaChange = viewModel::onBuscaChange,
                onDataChange = viewModel::onDataFiltroChange,
                onUsuarioChange = viewModel::onUsuarioFiltroChange,
                onLimpar = viewModel::limparFiltros
            )

            when (val s = state) {
                is HistoricoUiState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                is HistoricoUiState.Error -> ErroBox(
                    mensagem = s.mensagem,
                    onTentarNovamente = viewModel::carregarHistorico
                )

                is HistoricoUiState.Success -> ListaConteudo(itens = s.itens)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SecaoFiltros(
    filtros: FiltrosState,
    usuariosDisponiveis: List<Usuario>,
    onBuscaChange: (String) -> Unit,
    onDataChange: (String) -> Unit,
    onUsuarioChange: (String) -> Unit,
    onLimpar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = filtros.busca,
            onValueChange = onBuscaChange,
            label = { Text("Buscar medicamento...") },
            singleLine = true,
            trailingIcon = {
                if (filtros.busca.isNotEmpty()) {
                    IconButton(onClick = { onBuscaChange("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Limpar busca")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = filtros.dataFiltro,
                onValueChange = onDataChange,
                label = { Text("Data (dd/MM/yyyy)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )

            DropdownCuidador(
                usuariosDisponiveis = usuariosDisponiveis,
                usuarioIdSelecionado = filtros.usuarioIdFiltro,
                onUsuarioChange = onUsuarioChange,
                modifier = Modifier.weight(1f)
            )
        }

        if (filtros.temFiltroAtivo) {
            TextButton(onClick = onLimpar) {
                Text("Limpar filtros")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownCuidador(
    usuariosDisponiveis: List<Usuario>,
    usuarioIdSelecionado: String,
    onUsuarioChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val nomeSelecionado = if (usuarioIdSelecionado.isBlank()) {
        "Todos"
    } else {
        usuariosDisponiveis.firstOrNull { it.id == usuarioIdSelecionado }?.nome ?: "Cuidador"
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = nomeSelecionado,
            onValueChange = {},
            readOnly = true,
            label = { Text("Cuidador") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todos") },
                onClick = {
                    onUsuarioChange("")
                    expanded = false
                }
            )
            usuariosDisponiveis.forEach { usuario ->
                val id = usuario.id ?: return@forEach
                DropdownMenuItem(
                    text = { Text(usuario.nome.ifBlank { "(sem nome)" }) },
                    onClick = {
                        onUsuarioChange(id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ErroBox(mensagem: String, onTentarNovamente: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            style = MaterialTheme.typography.titleLarge
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
private fun ListaConteudo(itens: List<ItemHistorico>) {
    if (itens.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum registro encontrado.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = itens,
            key = { it.registro.id ?: "${it.registro.medicamentoId}-${it.registro.horarioConfirmacao}-${it.registro.data?.seconds}" }
        ) { item ->
            LinhaHistorico(item)
        }
    }
}

@Composable
private fun LinhaHistorico(item: ItemHistorico) {
    val tomado = item.registro.status == StatusMedicacao.TOMADO

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = if (tomado) Icons.Filled.CheckCircle else Icons.Outlined.Schedule,
                contentDescription = if (tomado) "Tomado" else "Pendente",
                tint = if (tomado) VerdeTomado else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(28.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.nomeMedicamento,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${item.registro.horarioConfirmacao.ifBlank { "—" }} · por ${item.nomeCuidador}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = item.registro.data?.toDataFormatada() ?: "Data desconhecida",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
