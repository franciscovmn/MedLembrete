package br.edu.ifpb.pdm.medlembrete.service

import br.edu.ifpb.pdm.medlembrete.model.Acompanhamento
import br.edu.ifpb.pdm.medlembrete.repository.AcompanhamentoRepository

class AcompanhamentoService(
    private val acompanhamentoRepository: AcompanhamentoRepository
) {

    suspend fun listarAcompanhamentosPorUsuario(
        usuarioId: String
    ): List<Acompanhamento> {
        return acompanhamentoRepository
            .listarAcompanhamentoPorUsuario(usuarioId)
    }

    suspend fun listarAcompanhamentosPorPaciente(
        pacienteId: String
    ): List<Acompanhamento> {
        return acompanhamentoRepository
            .listarAcompanhamentoPorPaciente(pacienteId)
    }

    suspend fun criarAcompanhamento(
        usuarioId: String,
        pacienteId: String
    ): Acompanhamento {
        val acompanhamento = Acompanhamento(
            usuarioId = usuarioId,
            pacienteId = pacienteId
        )

        return acompanhamentoRepository.salvarAcompanhamento(acompanhamento)
    }

    suspend fun excluirAcompanhamento(
        acompanhamento: Acompanhamento
    ) {
        acompanhamentoRepository.excluirAcompanhamento(acompanhamento)
    }
}