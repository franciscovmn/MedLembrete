package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao

data class ItemHistorico(
    val registro: RegistroMedicacao,
    val nomeMedicamento: String,
    val nomeCuidador: String
)

data class FiltrosState(
    val busca: String = "",
    val dataFiltro: String = "",
    val usuarioIdFiltro: String = ""
) {
    val temFiltroAtivo: Boolean
        get() = busca.isNotBlank() || dataFiltro.isNotBlank() || usuarioIdFiltro.isNotBlank()
}

sealed interface HistoricoUiState {
    data object Loading : HistoricoUiState
    data class Success(val itens: List<ItemHistorico>) : HistoricoUiState
    data class Error(val mensagem: String) : HistoricoUiState
}
