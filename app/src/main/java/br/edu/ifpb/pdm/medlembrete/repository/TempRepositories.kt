package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.Acompanhamento
import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao

/*
    Aqui entra a parte do Felipe:
    ele ficou responsável por criar os repositories,
    que fazem a comunicação com a API usando as classes do Retrofit.

    por enquanto, essas interfaces só retornam coisas vazias.
*/

interface MedicamentoRepository {
    suspend fun listarMedicamentos(): List<Medicamento> = emptyList()
    suspend fun listarPorPacienteId(pacienteId: String): List<Medicamento>

    suspend fun salvarMedicamento(medicamento: Medicamento)

    suspend fun atualizarMedicamento(medicamento: Medicamento)

    suspend fun excluirMedicamento(medicamentoId: String)
}

interface RegistroMedicacaoRepository {
    suspend fun listarRegistros(): List<RegistroMedicacao> = emptyList()
    suspend fun salvarRegistro(registro: RegistroMedicacao) {}
}

interface HorarioMedicacaoRepository {
    suspend fun salvarHorario(horario: HorarioMedicacao) {}
}

interface AcompanhamentoRepository {
    suspend fun salvarAcompanhamento(acompanhamento: Acompanhamento) {}
}