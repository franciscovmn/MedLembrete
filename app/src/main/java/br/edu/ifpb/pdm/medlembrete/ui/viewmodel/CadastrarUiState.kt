package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

const val HORARIO_PADRAO = "08:00"

data class CadastrarForm(
    val nome: String = "",
    val dosagem: String = "",
    val instrucoesUso: String = "",
    val nomeEn: String = "",
    val horarios: List<String> = listOf(HORARIO_PADRAO)
)

sealed interface CadastrarUiState {
    val form: CadastrarForm

    data class Editando(
        override val form: CadastrarForm = CadastrarForm(),
        val erro: String? = null
    ) : CadastrarUiState

    data class Salvando(override val form: CadastrarForm) : CadastrarUiState

    data class Sucesso(override val form: CadastrarForm) : CadastrarUiState
}
