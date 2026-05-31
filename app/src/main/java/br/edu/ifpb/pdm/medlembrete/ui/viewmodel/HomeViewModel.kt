package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.pdm.medlembrete.enums.StatusMedicacao
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao
import br.edu.ifpb.pdm.medlembrete.repository.HorarioMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.PacienteRepository
import br.edu.ifpb.pdm.medlembrete.repository.RegistroMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.RepositoryProvider
import br.edu.ifpb.pdm.medlembrete.repository.UsuarioRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// TODO: substituir por sessão real quando o login estiver implementado.
private const val USUARIO_ID_ATUAL = "MMmPLyyOPxe7DhI7jvh9"  // Murilo
private const val PACIENTE_ID_ATUAL = "kctuDccqdTHc6FKtBDtO"  // Roberto

class HomeViewModel(
    private val medicamentoRepository: MedicamentoRepository = RepositoryProvider.medicamentoRepository,
    private val horarioRepository: HorarioMedicacaoRepository = RepositoryProvider.horarioMedicacaoRepository,
    private val registroRepository: RegistroMedicacaoRepository = RepositoryProvider.registroMedicacaoRepository,
    private val usuarioRepository: UsuarioRepository = RepositoryProvider.usuarioRepository,
    private val pacienteRepository: PacienteRepository = RepositoryProvider.pacienteRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            registroRepository.observarRegistrosDoDia(PACIENTE_ID_ATUAL)
                .collect { _ -> carregarMedicamentosDoDia() }
        }
    }

    fun recarregar() {
        viewModelScope.launch { carregarMedicamentosDoDia() }
    }

    fun confirmarMedicacao(item: ItemHome) {
        val medicamentoId = item.medicamento.id ?: return
        viewModelScope.launch {
            try {
                val agora = Calendar.getInstance()
                val novoRegistro = RegistroMedicacao(
                    pacienteId = PACIENTE_ID_ATUAL,
                    medicamentoId = medicamentoId,
                    usuarioId = USUARIO_ID_ATUAL,
                    data = Timestamp(agora.time),
                    horarioProgramado = item.horario.horario,
                    horarioConfirmacao = formatoHora.format(agora.time),
                    status = StatusMedicacao.TOMADO
                )
                registroRepository.salvarRegistro(novoRegistro)
                // O snapshotListener vai disparar a recarga automaticamente,
                // mas chamamos manualmente para resposta imediata da UI.
                carregarMedicamentosDoDia()
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(
                    "Não foi possível confirmar: ${e.message ?: "erro desconhecido"}"
                )
            }
        }
    }

    private suspend fun carregarMedicamentosDoDia() {
        try {
            coroutineScope {
                val pacienteAsync = async { pacienteRepository.buscarPacientePorId(PACIENTE_ID_ATUAL) }
                val medicamentosAsync = async { medicamentoRepository.listarPorPacienteId(PACIENTE_ID_ATUAL) }
                val usuariosAsync = async { usuarioRepository.listarUsuarios() }
                val registrosDoDiaAsync = async {
                    registrosDoDia(registroRepository.listarRegistrosPorPaciente(PACIENTE_ID_ATUAL))
                }

                val paciente = pacienteAsync.await()
                val medicamentos = medicamentosAsync.await()
                val mapaUsuarios = usuariosAsync.await().associate { it.id to it.nome }
                val registrosDoDia = registrosDoDiaAsync.await()

                val horarios = medicamentos.mapNotNull { it.id }.flatMap { medId ->
                    horarioRepository.listarPorMedicamento(medId)
                }

                val itens = mutableListOf<ItemHome>()
                for (medicamento in medicamentos) {
                    val medId = medicamento.id ?: continue
                    val horariosDoMed = horarios.filter { it.medicamentoId == medId }
                    for (horario in horariosDoMed) {
                        val registro = registrosDoDia.firstOrNull { reg ->
                            reg.medicamentoId == medId &&
                                reg.horarioProgramado == horario.horario
                        }
                        val nomeCuidador = registro?.usuarioId?.let { mapaUsuarios[it] }
                        itens += ItemHome(medicamento, horario, registro, nomeCuidador)
                    }
                }

                val pendentes = itens.filter {
                    it.registro == null || it.registro.status == StatusMedicacao.PENDENTE
                }
                val tomados = itens.filter {
                    it.registro != null && it.registro.status == StatusMedicacao.TOMADO
                }

                _uiState.value = HomeUiState.Success(
                    pendentes = pendentes,
                    tomados = tomados,
                    nomePaciente = paciente.nome
                )
            }
        } catch (e: Exception) {
            _uiState.value = HomeUiState.Error(
                e.message ?: "Erro ao carregar medicamentos do dia."
            )
        }
    }

    private fun registrosDoDia(todos: List<RegistroMedicacao>): List<RegistroMedicacao> {
        val (inicio, fim) = intervaloDoDiaAtual()
        return todos.filter { reg ->
            val data = reg.data?.toDate() ?: return@filter false
            !data.before(inicio) && !data.after(fim)
        }
    }

    private fun intervaloDoDiaAtual(): Pair<Date, Date> {
        val inicio = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time
        val fim = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.time
        return inicio to fim
    }

    private companion object {
        val formatoHora: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    }
}
