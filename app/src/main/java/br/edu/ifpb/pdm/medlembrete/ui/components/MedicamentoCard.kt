package br.edu.ifpb.pdm.medlembrete.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import br.edu.ifpb.pdm.medlembrete.enums.StatusMedicacao
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.ItemHome

private val VerdeTomado = Color(0xFF2E7D32)

@Composable
fun MedicamentoCard(
    item: ItemHome,
    onConfirmar: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tomado = item.registro?.status == StatusMedicacao.TOMADO

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = item.medicamento.nome,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item.medicamento.dosagem,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = item.horario.horario,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (tomado) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Tomado",
                        tint = VerdeTomado,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    val confirmacao = item.registro.horarioConfirmacao
                    val programado = item.registro.horarioProgramado
                    val cuidador = item.nomeCuidador ?: "Cuidador"
                    val texto = if (programado.isBlank() || programado == confirmacao) {
                        "Tomado às $confirmacao por $cuidador"
                    } else {
                        "Tomado às $confirmacao (programado: $programado) por $cuidador"
                    }
                    Text(
                        text = texto,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Button(
                    onClick = onConfirmar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcar como tomado")
                }
            }
        }
    }
}
