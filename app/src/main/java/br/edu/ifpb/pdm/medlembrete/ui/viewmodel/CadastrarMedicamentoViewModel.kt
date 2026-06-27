package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.repository.AppEvents
import br.edu.ifpb.pdm.medlembrete.repository.HorarioMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val MAX_HORARIOS = 6

class CadastrarMedicamentoViewModel(
    private val medicamentoRepository: MedicamentoRepository,
    private val horarioRepository: HorarioMedicacaoRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CadastrarUiState>(CadastrarUiState.Editando())
    val uiState: StateFlow<CadastrarUiState> = _uiState.asStateFlow()

    /** Aplica uma transformação no formulário, só quando o estado é editável. Limpa o erro. */
    private fun atualizarForm(transform: (CadastrarForm) -> CadastrarForm) {
        val estado = _uiState.value
        if (estado !is CadastrarUiState.Editando) return
        _uiState.value = estado.copy(form = transform(estado.form), erro = null)
    }

    fun onNomeChange(novo: String) = atualizarForm { it.copy(nome = novo) }

    fun onDosagemChange(novo: String) = atualizarForm { it.copy(dosagem = novo) }

    fun onInstrucoesChange(novo: String) = atualizarForm { it.copy(instrucoesUso = novo) }

    fun onNomeEnChange(novo: String) = atualizarForm { it.copy(nomeEn = novo) }

    fun adicionarHorario() = atualizarForm { form ->
        if (form.horarios.size >= MAX_HORARIOS) form
        else form.copy(horarios = form.horarios + HORARIO_PADRAO)
    }

    fun removerHorario(index: Int) = atualizarForm { form ->
        if (form.horarios.size <= 1 || index !in form.horarios.indices) form
        else form.copy(horarios = form.horarios.toMutableList().apply { removeAt(index) })
    }

    fun atualizarHorario(index: Int, valor: String) = atualizarForm { form ->
        if (index !in form.horarios.indices) form
        else form.copy(horarios = form.horarios.toMutableList().apply { this[index] = valor })
    }

    fun salvar(pacienteId: String) {
        val estado = _uiState.value
        if (estado !is CadastrarUiState.Editando) return
        val form = estado.form

        val nome = form.nome.trim()
        val dosagem = form.dosagem.trim()
        val horariosLimpos = form.horarios
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()

        val erroValidacao = when {
            nome.isEmpty() -> "Informe o nome do medicamento."
            dosagem.isEmpty() -> "Informe a dosagem."
            horariosLimpos.isEmpty() -> "Informe pelo menos um horário."
            else -> null
        }
        if (erroValidacao != null) {
            _uiState.value = estado.copy(erro = erroValidacao)
            return
        }

        viewModelScope.launch {
            _uiState.value = CadastrarUiState.Salvando(form)
            try {
                val medicamento = Medicamento(
                    nome = nome,
                    nomeEn = form.nomeEn.trim().ifBlank { null },
                    dosagem = dosagem,
                    instrucoesUso = form.instrucoesUso.trim(),
                    pacienteId = pacienteId
                )
                val salvo = medicamentoRepository.salvarMedicamento(medicamento)
                val medId = requireNotNull(salvo.id) {
                    "Medicamento salvo sem ID retornado pelo repositório."
                }

                coroutineScope {
                    horariosLimpos.map { hora ->
                        async {
                            horarioRepository.salvarHorario(
                                HorarioMedicacao(
                                    medicamentoId = medId,
                                    horario = hora
                                )
                            )
                        }
                    }.awaitAll()
                }

                _uiState.value = CadastrarUiState.Sucesso(form)
                AppEvents.medicamentoCadastrado.tryEmit(Unit)
            } catch (e: Exception) {
                _uiState.value = CadastrarUiState.Editando(
                    form = form,
                    erro = "Não foi possível salvar: ${e.message ?: "erro desconhecido"}"
                )
            }
        }
    }
}
