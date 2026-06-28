package br.edu.ifpb.pdm.medlembrete.repository.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representação do medicamento na tabela local do Room.
 * Espelha o modelo de domínio [br.edu.ifpb.pdm.medlembrete.model.Medicamento],
 * servindo como cache offline dos dados que vêm do Firestore.
 */
@Entity(tableName = "medicamentos")
data class MedicamentoEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val nomeEn: String?,
    val dosagem: String,
    val instrucoesUso: String,
    val pacienteId: String
)
