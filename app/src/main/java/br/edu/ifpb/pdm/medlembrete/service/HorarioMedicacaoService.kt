package br.edu.ifpb.pdm.medlembrete.service

import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import br.edu.ifpb.pdm.medlembrete.repository.HorarioMedicacaoRepository

class HorarioMedicacaoService(
    private val horarioRepository: HorarioMedicacaoRepository
) {

    suspend fun listarHorarios(): List<HorarioMedicacao> {
        return horarioRepository.listarHorariosMedicacaoPorMedicamento()
    }

    suspend fun listarHorariosPorMedicamento(
        medicamentoId: String
    ): List<HorarioMedicacao> {
        return horarioRepository.listarPorMedicamento(medicamentoId)
    }

    suspend fun criarHorario(
        medicamentoId: String,
        horario: String
    ): HorarioMedicacao {
        val novoHorario = HorarioMedicacao(
            medicamentoId = medicamentoId,
            horario = horario
        )

        return horarioRepository.salvarHorario(novoHorario)
    }

    suspend fun editarHorario(
        horarioId: String,
        medicamentoId: String,
        horario: String
    ): HorarioMedicacao {
        val horarioAtualizado = HorarioMedicacao(
            id = horarioId,
            medicamentoId = medicamentoId,
            horario = horario
        )

        return horarioRepository.editarHorario(horarioAtualizado)
    }

    suspend fun excluirHorario(horario: HorarioMedicacao) {
        horarioRepository.excluirHorario(horario)
    }
}