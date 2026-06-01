package br.edu.ifpb.pdm.medlembrete.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.pdm.medlembrete.enums.StatusMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.model.MedicamentoInfoExterna
import br.edu.ifpb.pdm.medlembrete.ui.util.toDataFormatada
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.DetalheUiState
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.DetalheViewModel
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.RegistroComCuidador

private val VerdeTomado = Color(0xFF2E7D32)
private const val LIMITE_TEXTO_BULA = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalheMedicamentoScreen(
    medicamentoId: String,
    onVoltar: () -> Unit,
    viewModel: DetalheViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(medicamentoId) {
        viewModel.carregarDetalhe(medicamentoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhe do Medicamento") },
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
        when (val s = state) {
            is DetalheUiState.Loading -> CarregandoBox(innerPadding)
            is DetalheUiState.Error -> ErroBox(
                mensagem = s.mensagem,
                onTentarNovamente = { viewModel.recarregar(medicamentoId) },
                paddingValues = innerPadding
            )
            is DetalheUiState.Success -> SucessoConteudo(
                medicamento = s.medicamento,
                infoExterna = s.infoExterna,
                registros = s.registros,
                paddingValues = innerPadding
            )
        }
    }
}

@Composable
private fun CarregandoBox(paddingValues: PaddingValues) {
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
private fun ErroBox(
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
private fun SucessoConteudo(
    medicamento: Medicamento,
    infoExterna: MedicamentoInfoExterna?,
    registros: List<RegistroComCuidador>,
    paddingValues: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { SecaoMedicamento(medicamento) }
        item { HorizontalDivider() }
        item { SecaoBula(infoExterna) }
        item { HorizontalDivider() }
        item { CabecalhoHistorico() }

        if (registros.isEmpty()) {
            item {
                Text(
                    text = "Nenhum registro ainda.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(
                items = registros,
                key = { it.registro.id ?: "${it.registro.medicamentoId}-${it.registro.horarioConfirmacao}" }
            ) { item ->
                LinhaRegistro(item)
            }
        }
    }
}

@Composable
private fun SecaoMedicamento(medicamento: Medicamento) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = medicamento.nome,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = medicamento.dosagem,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "Instruções de uso:",
                style = MaterialTheme.typography.titleSmall
            )
        }
        Text(
            text = medicamento.instrucoesUso.ifBlank { "Sem instruções específicas." },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SecaoBula(infoExterna: MedicamentoInfoExterna?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "📋 Informações da Bula",
            style = MaterialTheme.typography.titleLarge
        )

        if (infoExterna == null) {
            Text(
                text = "Informações da bula não disponíveis para este medicamento.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        infoExterna.genericName?.takeIf { it.isNotBlank() }?.let { gen ->
            Text(
                text = "Princípio ativo: $gen",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        ItemBula(rotulo = "Indicações", texto = infoExterna.indicacoes)
        ItemBula(rotulo = "Advertências", texto = infoExterna.avisos)
        ItemBula(rotulo = "Dosagem", texto = infoExterna.posologia)
    }
}

@Composable
private fun ItemBula(rotulo: String, texto: String?) {
    if (texto.isNullOrBlank()) return
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = "$rotulo:",
            style = MaterialTheme.typography.titleSmall
        )
        Text(
            text = texto.take(LIMITE_TEXTO_BULA) + if (texto.length > LIMITE_TEXTO_BULA) "..." else "",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun CabecalhoHistorico() {
    Text(
        text = "📅 Histórico de administrações",
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
private fun LinhaRegistro(item: RegistroComCuidador) {
    val tomado = item.registro.status == StatusMedicacao.TOMADO

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = if (tomado) Icons.Filled.CheckCircle else Icons.Outlined.Schedule,
            contentDescription = if (tomado) "Tomado" else "Pendente",
            tint = if (tomado) VerdeTomado else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "${item.registro.horarioConfirmacao.ifBlank { "—" }} · por ${item.nomeCuidador}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = item.registro.data?.toDataFormatada() ?: "Data desconhecida",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
