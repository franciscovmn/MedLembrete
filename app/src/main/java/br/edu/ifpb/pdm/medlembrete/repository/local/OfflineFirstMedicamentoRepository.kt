package br.edu.ifpb.pdm.medlembrete.repository.local

import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository


class OfflineFirstMedicamentoRepository(
    private val remoto: MedicamentoRepository,
    private val dao: MedicamentoDao,
) : MedicamentoRepository {

    override suspend fun listarMedicamentos(): List<Medicamento> = try {
        val medicamentos = remoto.listarMedicamentos()
        dao.inserirTodos(medicamentos.map { it.toEntity() })
        medicamentos
    } catch (e: Exception) {
        dao.listarTodos().map { it.toDomain() }
    }

    override suspend fun listarPorPacienteId(pacienteId: String): List<Medicamento> = try {
        val medicamentos = remoto.listarPorPacienteId(pacienteId)
        dao.limparPorPaciente(pacienteId)
        dao.inserirTodos(medicamentos.map { it.toEntity() })
        medicamentos
    } catch (e: Exception) {
        dao.listarPorPaciente(pacienteId).map { it.toDomain() }
    }

    override suspend fun buscarMedicamentoPorID(medicamentoId: String): Medicamento = try {
        val medicamento = remoto.buscarMedicamentoPorID(medicamentoId)
        dao.inserir(medicamento.toEntity())
        medicamento
    } catch (e: Exception) {
        dao.buscarPorId(medicamentoId)?.toDomain() ?: throw e
    }

    override suspend fun salvarMedicamento(medicamento: Medicamento): Medicamento {
        val salvo = remoto.salvarMedicamento(medicamento)
        dao.inserir(salvo.toEntity())
        return salvo
    }

    override suspend fun atualizarMedicamento(medicamento: Medicamento): Medicamento {
        val atualizado = remoto.atualizarMedicamento(medicamento)
        dao.inserir(atualizado.toEntity())
        return atualizado
    }

    override suspend fun excluirMedicamento(medicamentoId: String) {
        remoto.excluirMedicamento(medicamentoId)
        dao.excluirPorId(medicamentoId)
    }
}
