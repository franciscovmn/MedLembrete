package br.edu.ifpb.pdm.medlembrete.service

import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao
import br.edu.ifpb.pdm.medlembrete.enums.StatusMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.repository.HorarioMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.RegistroMedicacaoRepository
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class MedicamentoService(
    private val medicamentoRepository: MedicamentoRepository,
    private val registroRepository: RegistroMedicacaoRepository,
    private val horarioRepository: HorarioMedicacaoRepository,
) {
    suspend fun listarMedicamentos(pacienteId: String): List<Medicamento> {
        return medicamentoRepository.listarPorPacienteId(pacienteId)
    }

    // CRIAR MEDICAMENTO
    suspend fun criarMedicamento(
        nome: String,
        dosagem: String,
        instrucoesUso: String,
        pacienteId: String
    ) {
        val medicamento = Medicamento(
            nome = nome,
            dosagem = dosagem,
            instrucoesUso = instrucoesUso,
            pacienteId = pacienteId
        )

        medicamentoRepository.salvarMedicamento(medicamento)
    }

    // EDITAR MEDICAMENTO
    suspend fun editarMedicamento(
        medicamentoId: String,
        nome: String,
        dosagem: String,
        instrucoesUso: String
    ) {
        val medicamentoAtualizado = Medicamento(
            id = medicamentoId,
            nome = nome,
            dosagem = dosagem,
            instrucoesUso = instrucoesUso
        )

        medicamentoRepository.atualizarMedicamento(medicamentoAtualizado)
    }

    // EXCLUIR MEDICAMENTO
    suspend fun excluirMedicamento(medicamentoId: String) {
        medicamentoRepository.excluirMedicamento(medicamentoId)
    }

    suspend fun marcarMedicamentoComoTomado(
        pacienteId: String,
        medicamentoId: String,
        usuarioId: String,
        data: String,
        horarioConfirmacao: String
    ) {
        val registro = RegistroMedicacao(
            pacienteId = pacienteId,
            medicamentoId = medicamentoId,
            usuarioId = usuarioId,
            data = converterStringParaTimestamp(data),
            horarioConfirmacao = horarioConfirmacao,
            status = StatusMedicacao.TOMADO
        )

        registroRepository.salvarRegistro(registro)
    }

    suspend fun salvarHorario(
        pacienteId: String,
        medicamentoId: String,
        horario: String,
        cuidadorId: String
    ) {
        val novoHorario = HorarioMedicacao(medicamentoId = medicamentoId,horario = horario)
        horarioRepository.salvarHorario(novoHorario)
    }

    //metodo util pra converter data de String para TimeStamp
    private fun converterStringParaTimestamp(data: String): Timestamp {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date = formato.parse(data)
            ?: throw IllegalArgumentException("Data inválida: $data")

        return Timestamp(date)
    }
}