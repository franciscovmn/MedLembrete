package br.edu.ifpb.pdm.medlembrete.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.CadastrarMedicamentoViewModel

private const val PACIENTE_ID_ATUAL = "kctuDccqdTHc6FKtBDtO"  // Roberto — TODO: substituir por sessão real
private const val MAX_HORARIOS = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CadastrarMedicamentoScreen(
    onVoltar: () -> Unit,
    onSucesso: () -> Unit,
    viewModel: CadastrarMedicamentoViewModel = viewModel()
) {
    val form by viewModel.form.collectAsStateWithLifecycle()

    LaunchedEffect(form.sucesso) {
        if (form.sucesso) onSucesso()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Medicamento") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = form.nome,
                onValueChange = viewModel::onNomeChange,
                label = { Text("Nome do medicamento*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.dosagem,
                onValueChange = viewModel::onDosagemChange,
                label = { Text("Dosagem* (ex: 500mg, 2 comprimidos)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.instrucoesUso,
                onValueChange = viewModel::onInstrucoesChange,
                label = { Text("Instruções de uso*") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.nomeEn,
                onValueChange = viewModel::onNomeEnChange,
                label = { Text("Nome em inglês (opcional)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Usado para buscar informações da bula. Ex: Dipirona → Dipyrone",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = "Horários de administração",
                style = MaterialTheme.typography.titleMedium
            )

            form.horarios.forEachIndexed { index, valor ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = valor,
                        onValueChange = { viewModel.atualizarHorario(index, it) },
                        label = { Text("Horário (HH:mm)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    if (form.horarios.size > 1) {
                        IconButton(onClick = { viewModel.removerHorario(index) }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Remover horário"
                            )
                        }
                    }
                }
            }

            TextButton(
                onClick = viewModel::adicionarHorario,
                enabled = form.horarios.size < MAX_HORARIOS
            ) {
                Text("+ Adicionar horário")
            }

            form.erro?.let { erro ->
                Text(
                    text = erro,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = { viewModel.salvar(PACIENTE_ID_ATUAL) },
                enabled = !form.salvando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar medicamento")
            }

            if (form.salvando) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
