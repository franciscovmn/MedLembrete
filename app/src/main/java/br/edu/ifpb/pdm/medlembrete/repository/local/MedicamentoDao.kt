package br.edu.ifpb.pdm.medlembrete.repository.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

/**
 * Operações de acesso à tabela local de medicamentos.
 * Todas as funções são `suspend`: o Room executa fora da main thread.
 */
@Dao
interface MedicamentoDao {

    @Upsert
    suspend fun inserirTodos(medicamentos: List<MedicamentoEntity>)

    @Upsert
    suspend fun inserir(medicamento: MedicamentoEntity)

    @Query("SELECT * FROM medicamentos")
    suspend fun listarTodos(): List<MedicamentoEntity>

    @Query("SELECT * FROM medicamentos WHERE pacienteId = :pacienteId")
    suspend fun listarPorPaciente(pacienteId: String): List<MedicamentoEntity>

    @Query("SELECT * FROM medicamentos WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: String): MedicamentoEntity?

    @Query("DELETE FROM medicamentos WHERE id = :id")
    suspend fun excluirPorId(id: String)

    @Query("DELETE FROM medicamentos WHERE pacienteId = :pacienteId")
    suspend fun limparPorPaciente(pacienteId: String)
}
