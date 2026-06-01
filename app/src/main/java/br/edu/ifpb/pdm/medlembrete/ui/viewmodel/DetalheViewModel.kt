package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.OpenFdaRepository
import br.edu.ifpb.pdm.medlembrete.repository.RegistroMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.RepositoryProvider
import br.edu.ifpb.pdm.medlembrete.repository.UsuarioRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalheViewModel(
    private val medicamentoRepository: MedicamentoRepository = RepositoryProvider.medicamentoRepository,
    private val registroRepository: RegistroMedicacaoRepository = RepositoryProvider.registroMedicacaoRepository,
    private val usuarioRepository: UsuarioRepository = RepositoryProvider.usuarioRepository,
    private val openFdaRepository: OpenFdaRepository = OpenFdaRepository(),
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetalheUiState>(DetalheUiState.Loading)
    val uiState: StateFlow<DetalheUiState> = _uiState.asStateFlow()

    fun carregarDetalhe(medicamentoId: String) {
        viewModelScope.launch {
            _uiState.value = DetalheUiState.Loading
            try {
                coroutineScope {
                    val medicamentoAsync = async { medicamentoRepository.buscarMedicamentoPorID(medicamentoId) }
                    val registrosBrutosAsync = async {
                        registroRepository.listarRegistrosPorMedicamento(medicamentoId)
                            .sortedByDescending { it.data?.toDate()?.time ?: 0L }
                            .take(10)
                    }

                    val medicamento = medicamentoAsync.await()
                    val registrosBrutos = registrosBrutosAsync.await()

                    // OpenFDA é opcional — nunca derruba a tela.
                    val infoExternaAsync = async {
                        openFdaRepository.buscarInfoMedicamento(medicamento.nome).getOrNull()
                    }

                    // Cada cuidador é buscado em paralelo via doc-level read.
                    val registrosComCuidadorAsync = registrosBrutos.map { reg ->
                        async {
                            val nome = if (reg.usuarioId.isBlank()) {
                                "Cuidador"
                            } else {
                                runCatching { usuarioRepository.buscarUsuarioPorId(reg.usuarioId).nome }
                                    .getOrDefault("Cuidador")
                            }
                            RegistroComCuidador(reg, nome)
                        }
                    }

                    val infoExterna = infoExternaAsync.await()
                    val registrosComCuidador = registrosComCuidadorAsync.awaitAll()

                    _uiState.value = DetalheUiState.Success(
                        medicamento = medicamento,
                        infoExterna = infoExterna,
                        registros = registrosComCuidador
                    )
                }
            } catch (e: Exception) {
                _uiState.value = DetalheUiState.Error(
                    e.message ?: "Erro ao carregar detalhe do medicamento."
                )
            }
        }
    }

    fun recarregar(medicamentoId: String) {
        carregarDetalhe(medicamentoId)
    }
}
