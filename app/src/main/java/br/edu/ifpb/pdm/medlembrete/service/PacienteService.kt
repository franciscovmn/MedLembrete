package br.edu.ifpb.pdm.medlembrete.service

import br.edu.ifpb.pdm.medlembrete.model.Paciente
import br.edu.ifpb.pdm.medlembrete.repository.PacienteRepository
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class PacienteService(
    private val pacienteRepository: PacienteRepository
) {

    suspend fun listarPacientes(): List<Paciente> {
        return pacienteRepository.listarPacientes()
    }

    suspend fun listarPacientesPorUsuario(usuarioId: String): List<Paciente> {
        return pacienteRepository.listarPacientesPorUsuario(usuarioId)
    }

    suspend fun buscarPacientePorId(pacienteId: String): Paciente {
        return pacienteRepository.buscarPacientePorId(pacienteId)
    }

    suspend fun criarPaciente(
        nome: String,
        dataNascimento: String
    ): Paciente {
        val paciente = Paciente(
            nome = nome,
            dataNascimento = converterStringParaTimestamp(dataNascimento)
        )

        return pacienteRepository.salvarPaciente(paciente)
    }

    suspend fun editarPaciente(
        pacienteId: String,
        nome: String,
        dataNascimento: String
    ): Paciente {
        val pacienteAtualizado = Paciente(
            id = pacienteId,
            nome = nome,
            dataNascimento = converterStringParaTimestamp(dataNascimento)
        )

        return pacienteRepository.atualizarPaciente(pacienteAtualizado)
    }

    suspend fun excluirPaciente(paciente: Paciente) {
        pacienteRepository.excluirPaciente(paciente)
    }

    private fun converterStringParaTimestamp(data: String): Timestamp {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val date = formato.parse(data)
            ?: throw IllegalArgumentException("Data inválida: $data")

        return Timestamp(date)
    }
}