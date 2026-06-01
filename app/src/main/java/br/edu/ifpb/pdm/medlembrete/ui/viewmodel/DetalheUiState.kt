package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.model.MedicamentoInfoExterna
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao

data class RegistroComCuidador(
    val registro: RegistroMedicacao,
    val nomeCuidador: String
)

sealed interface DetalheUiState {
    data object Loading : DetalheUiState
    data class Success(
        val medicamento: Medicamento,
        val infoExterna: MedicamentoInfoExterna?,
        val registros: List<RegistroComCuidador>
    ) : DetalheUiState
    data class Error(val mensagem: String) : DetalheUiState
}
