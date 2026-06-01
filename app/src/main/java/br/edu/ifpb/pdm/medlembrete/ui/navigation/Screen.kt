package br.edu.ifpb.pdm.medlembrete.ui.navigation

sealed class Screen(val route: String) {

    data object Home : Screen("home")

    data object DetalheMedicamento : Screen("detalhe/{medicamentoId}") {
        const val ARG_MEDICAMENTO_ID = "medicamentoId"
        fun createRoute(medicamentoId: String): String = "detalhe/$medicamentoId"
    }

    data object Historico : Screen("historico")

    data object CadastrarMedicamento : Screen("cadastrar-medicamento")
}
