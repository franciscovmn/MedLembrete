package br.edu.ifpb.pdm.medlembrete.service

import br.edu.ifpb.pdm.medlembrete.enums.StatusMedicacao
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao
import br.edu.ifpb.pdm.medlembrete.repository.RegistroMedicacaoRepository
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class RegistroMedicacaoService(
    private val registroRepository: RegistroMedicacaoRepository
) {

    suspend fun listarRegistros(): List<RegistroMedicacao> {
        return registroRepository.listarRegistros()
    }

    suspend fun listarRegistrosPorPaciente(
        pacienteId: String
    ): List<RegistroMedicacao> {
        return registroRepository.listarRegistrosPorPaciente(pacienteId)
    }

    suspend fun listarRegistrosPorUsuario(
        usuarioId: String
    ): List<RegistroMedicacao> {
        return registroRepository.listarRegistrosPorUsuario(usuarioId)
    }

    suspend fun listarRegistrosPorMedicamento(
        medicamentoId: String
    ): List<RegistroMedicacao> {
        return registroRepository.listarRegistrosPorMedicamento(medicamentoId)
    }

    suspend fun criarRegistro(
        pacienteId: String,
        medicamentoId: String,
        usuarioId: String,
        data: String,
        horarioConfirmacao: String,
        status: StatusMedicacao
    ): RegistroMedicacao {
        val registro = RegistroMedicacao(
            pacienteId = pacienteId,
            medicamentoId = medicamentoId,
            usuarioId = usuarioId,
            data = converterStringParaTimestamp(data),
            horarioConfirmacao = horarioConfirmacao,
            status = status
        )

        return registroRepository.salvarRegistro(registro)
    }

    suspend fun editarRegistro(
        registroId: String,
        pacienteId: String,
        medicamentoId: String,
        usuarioId: String,
        data: String,
        horarioConfirmacao: String,
        status: StatusMedicacao
    ): RegistroMedicacao {
        val registroAtualizado = RegistroMedicacao(
            id = registroId,
            pacienteId = pacienteId,
            medicamentoId = medicamentoId,
            usuarioId = usuarioId,
            data = converterStringParaTimestamp(data),
            horarioConfirmacao = horarioConfirmacao,
            status = status
        )

        return registroRepository.atualizarRegistro(registroAtualizado)
    }

    suspend fun excluirRegistro(registro: RegistroMedicacao) {
        registroRepository.excluirRegistro(registro)
    }

    private fun converterStringParaTimestamp(data: String): Timestamp {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date = formato.parse(data)
            ?: throw IllegalArgumentException("Data inválida: $data")

        return Timestamp(date)
    }
}