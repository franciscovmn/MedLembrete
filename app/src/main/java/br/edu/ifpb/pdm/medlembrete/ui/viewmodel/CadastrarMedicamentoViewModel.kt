package br.edu.ifpb.pdm.medlembrete.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.repository.HorarioMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.RepositoryProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val HORARIO_PADRAO = "08:00"
private const val MAX_HORARIOS = 6

data class CadastrarForm(
    val nome: String = "",
    val dosagem: String = "",
    val instrucoesUso: String = "",
    val nomeEn: String = "",
    val horarios: List<String> = listOf(HORARIO_PADRAO),
    val salvando: Boolean = false,
    val erro: String? = null,
    val sucesso: Boolean = false
)

class CadastrarMedicamentoViewModel(
    private val medicamentoRepository: MedicamentoRepository = RepositoryProvider.medicamentoRepository,
    private val horarioRepository: HorarioMedicacaoRepository = RepositoryProvider.horarioMedicacaoRepository,
) : ViewModel() {

    private val _form = MutableStateFlow(CadastrarForm())
    val form: StateFlow<CadastrarForm> = _form.asStateFlow()

    fun onNomeChange(novo: String) {
        _form.value = _form.value.copy(nome = novo, erro = null)
    }

    fun onDosagemChange(novo: String) {
        _form.value = _form.value.copy(dosagem = novo, erro = null)
    }

    fun onInstrucoesChange(novo: String) {
        _form.value = _form.value.copy(instrucoesUso = novo, erro = null)
    }

    fun onNomeEnChange(novo: String) {
        _form.value = _form.value.copy(nomeEn = novo, erro = null)
    }

    fun adicionarHorario() {
        val atual = _form.value.horarios
        if (atual.size >= MAX_HORARIOS) return
        _form.value = _form.value.copy(horarios = atual + HORARIO_PADRAO, erro = null)
    }

    fun removerHorario(index: Int) {
        val atual = _form.value.horarios
        if (atual.size <= 1) return
        if (index !in atual.indices) return
        _form.value = _form.value.copy(
            horarios = atual.toMutableList().apply { removeAt(index) },
            erro = null
        )
    }

    fun atualizarHorario(index: Int, valor: String) {
        val atual = _form.value.horarios
        if (index !in atual.indices) return
        _form.value = _form.value.copy(
            horarios = atual.toMutableList().apply { this[index] = valor },
            erro = null
        )
    }

    fun salvar(pacienteId: String) {
        val estado = _form.value

        val nome = estado.nome.trim()
        val dosagem = estado.dosagem.trim()
        val horariosLimpos = estado.horarios
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
            _form.value = estado.copy(erro = erroValidacao)
            return
        }

        viewModelScope.launch {
            _form.value = estado.copy(salvando = true, erro = null)
            try {
                val medicamento = Medicamento(
                    nome = nome,
                    nomeEn = estado.nomeEn.trim().ifBlank { null },
                    dosagem = dosagem,
                    instrucoesUso = estado.instrucoesUso.trim(),
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

                _form.value = _form.value.copy(salvando = false, sucesso = true)
            } catch (e: Exception) {
                _form.value = _form.value.copy(
                    salvando = false,
                    erro = "Não foi possível salvar: ${e.message ?: "erro desconhecido"}"
                )
            }
        }
    }
}
