package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.pdm.medlembrete.repository.AppEvents
import br.edu.ifpb.pdm.medlembrete.repository.HorarioMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.OpenFdaRepository
import br.edu.ifpb.pdm.medlembrete.repository.RegistroMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.RepositoryProvider
import br.edu.ifpb.pdm.medlembrete.repository.UsuarioRepository
import br.edu.ifpb.pdm.medlembrete.ui.util.resolverNomeEn
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetalheViewModel(
    private val medicamentoRepository: MedicamentoRepository,
    private val registroRepository: RegistroMedicacaoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val horarioRepository: HorarioMedicacaoRepository,
    private val openFdaRepository: OpenFdaRepository,
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

                    val infoExternaAsync = async {
                        val nomeParaBusca = resolverNomeEn(medicamento.nome, medicamento.nomeEn)
                        if (nomeParaBusca != null) {
                            openFdaRepository.buscarInfoMedicamento(nomeParaBusca).getOrNull()
                        } else {
                            null
                        }
                    }

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

    fun excluirMedicamento(medicamentoId: String, onSucesso: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = DetalheUiState.Loading
            try {
                val horarios = horarioRepository.listarPorMedicamento(medicamentoId)
                coroutineScope {
                    horarios.map { h ->
                        async { horarioRepository.excluirHorario(h) }
                    }.awaitAll()
                }

                medicamentoRepository.excluirMedicamento(medicamentoId)

                AppEvents.medicamentoCadastrado.tryEmit(Unit)
                onSucesso()
            } catch (e: Exception) {
                _uiState.value = DetalheUiState.Error(
                    e.message ?: "Erro ao excluir medicamento."
                )
            }
        }
    }
}
