package br.edu.ifpb.pdm.medlembrete

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import br.edu.ifpb.pdm.medlembrete.ui.navigation.Screen
import br.edu.ifpb.pdm.medlembrete.ui.screens.CadastrarMedicamentoScreen
import br.edu.ifpb.pdm.medlembrete.ui.screens.DetalheMedicamentoScreen
import br.edu.ifpb.pdm.medlembrete.ui.screens.HistoricoScreen
import br.edu.ifpb.pdm.medlembrete.ui.screens.HomeScreen
import br.edu.ifpb.pdm.medlembrete.ui.theme.MedLembreteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MedLembreteTheme {
                MedLembreteApp()
            }
        }
    }
}

@Composable
fun MedLembreteApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                onAbrirDetalhe = { id ->
                    navController.navigate(Screen.DetalheMedicamento.createRoute(id))
                },
                onAbrirHistorico = {
                    navController.navigate(Screen.Historico.route)
                },
                onAbrirCadastrarMedicamento = {
                    navController.navigate(Screen.CadastrarMedicamento.route)
                }
            )
        }

        composable(route = Screen.CadastrarMedicamento.route) {
            CadastrarMedicamentoScreen(
                onVoltar = { navController.popBackStack() },
                onSucesso = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.DetalheMedicamento.route,
            arguments = listOf(
                navArgument(Screen.DetalheMedicamento.ARG_MEDICAMENTO_ID) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments
                ?.getString(Screen.DetalheMedicamento.ARG_MEDICAMENTO_ID)
                .orEmpty()

            DetalheMedicamentoScreen(
                medicamentoId = id,
                onVoltar = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Historico.route) {
            HistoricoScreen(
                onVoltar = { navController.popBackStack() }
            )
        }
    }
}
