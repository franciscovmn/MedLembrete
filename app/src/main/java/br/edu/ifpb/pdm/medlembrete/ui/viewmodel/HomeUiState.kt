package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao

data class ItemHome(
    val medicamento: Medicamento,
    val horario: HorarioMedicacao,
    val registro: RegistroMedicacao?,
    val nomeCuidador: String?
)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val pendentes: List<ItemHome>,
        val tomados: List<ItemHome>,
        val nomePaciente: String
    ) : HomeUiState
    data class Error(val mensagem: String) : HomeUiState
}
