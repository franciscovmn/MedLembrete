package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.pdm.medlembrete.model.Usuario
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository
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

// TODO: substituir por sessão real quando o login estiver implementado.
private const val PACIENTE_ID_ATUAL = "kctuDccqdTHc6FKtBDtO"  // Roberto

class HistoricoViewModel(
    private val registroRepository: RegistroMedicacaoRepository = RepositoryProvider.registroMedicacaoRepository,
    private val medicamentoRepository: MedicamentoRepository = RepositoryProvider.medicamentoRepository,
    private val usuarioRepository: UsuarioRepository = RepositoryProvider.usuarioRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoricoUiState>(HistoricoUiState.Loading)
    val uiState: StateFlow<HistoricoUiState> = _uiState.asStateFlow()

    private val _filtros = MutableStateFlow(FiltrosState())
    val filtros: StateFlow<FiltrosState> = _filtros.asStateFlow()

    private val _usuariosDisponiveis = MutableStateFlow<List<Usuario>>(emptyList())
    val usuariosDisponiveis: StateFlow<List<Usuario>> = _usuariosDisponiveis.asStateFlow()

    init {
        viewModelScope.launch {
            _usuariosDisponiveis.value = runCatching { usuarioRepository.listarUsuarios() }
                .getOrDefault(emptyList())
        }
    }

    fun onBuscaChange(texto: String) {
        _filtros.value = _filtros.value.copy(busca = texto)
        carregarHistorico()
    }

    fun onDataFiltroChange(data: String) {
        _filtros.value = _filtros.value.copy(dataFiltro = data)
        carregarHistorico()
    }

    fun onUsuarioFiltroChange(usuarioId: String) {
        _filtros.value = _filtros.value.copy(usuarioIdFiltro = usuarioId)
        carregarHistorico()
    }

    fun limparFiltros() {
        _filtros.value = FiltrosState()
        carregarHistorico()
    }

    fun carregarHistorico() {
        viewModelScope.launch {
            _uiState.value = HistoricoUiState.Loading
            try {
                val f = _filtros.value
                val registros = registroRepository.listarTodosRegistrosComFiltro(
                    pacienteId = PACIENTE_ID_ATUAL,
                    nomeMedicamento = f.busca.ifBlank { null },
                    data = f.dataFiltro.ifBlank { null },
                    usuarioId = f.usuarioIdFiltro.ifBlank { null }
                )

                val itens = coroutineScope {
                    registros.map { reg ->
                        async {
                            val nomeMed = runCatching {
                                medicamentoRepository.buscarMedicamentoPorID(reg.medicamentoId).nome
                            }.getOrDefault("Medicamento")

                            val nomeCui = if (reg.usuarioId.isBlank()) {
                                "Cuidador"
                            } else {
                                runCatching {
                                    usuarioRepository.buscarUsuarioPorId(reg.usuarioId).nome
                                }.getOrDefault("Cuidador")
                            }

                            ItemHistorico(reg, nomeMed, nomeCui)
                        }
                    }.awaitAll()
                }

                _uiState.value = HistoricoUiState.Success(itens)
            } catch (e: Exception) {
                _uiState.value = HistoricoUiState.Error(
                    e.message ?: "Erro ao carregar histórico."
                )
            }
        }
    }
}
