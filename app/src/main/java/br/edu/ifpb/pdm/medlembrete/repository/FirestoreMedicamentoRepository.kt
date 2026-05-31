package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirestoreMedicamentoRepository(
    firestore: FirebaseFirestore = Firebase.firestore
) : MedicamentoRepository {

    private val collection = firestore.collection(COLLECTION)

    override suspend fun listarMedicamentos(): List<Medicamento> = try {
        collection.get().await().documents.mapNotNull { doc ->
            doc.toObject(Medicamento::class.java)?.copy(id = doc.id)
        }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun buscarMedicamentoPorID(medicamentoId: String): Medicamento {
        val doc = collection.document(medicamentoId).get().await()
        val medicamento = doc.toObject(Medicamento::class.java)
            ?: error("Medicamento '$medicamentoId' não encontrado.")
        return medicamento.copy(id = doc.id)
    }

    override suspend fun listarPorPacienteId(pacienteId: String): List<Medicamento> = try {
        collection.whereEqualTo("pacienteId", pacienteId).get().await()
            .documents.mapNotNull { doc ->
                doc.toObject(Medicamento::class.java)?.copy(id = doc.id)
            }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun salvarMedicamento(medicamento: Medicamento): Medicamento {
        val ref = collection.add(medicamento).await()
        return medicamento.copy(id = ref.id)
    }

    override suspend fun atualizarMedicamento(medicamento: Medicamento): Medicamento {
        val id = requireNotNull(medicamento.id) { "Medicamento sem ID não pode ser atualizado." }
        collection.document(id).set(medicamento).await()
        return medicamento
    }

    override suspend fun excluirMedicamento(medicamentoId: String) {
        collection.document(medicamentoId).delete().await()
    }

    private companion object {
        const val COLLECTION = "Medicamento"
    }
}
