package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.Acompanhamento
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirestoreAcompanhamentoRepository(
    firestore: FirebaseFirestore = Firebase.firestore
) : AcompanhamentoRepository {

    private val collection = firestore.collection(COLLECTION)

    override suspend fun salvarAcompanhamento(acompanhamento: Acompanhamento): Acompanhamento {
        val ref = collection.add(acompanhamento).await()
        return acompanhamento.copy(id = ref.id)
    }

    override suspend fun excluirAcompanhamento(acompanhamento: Acompanhamento) {
        val id = requireNotNull(acompanhamento.id) {
            "Acompanhamento sem ID não pode ser excluído."
        }
        collection.document(id).delete().await()
    }

    override suspend fun listarAcompanhamentoPorUsuario(usuarioId: String): List<Acompanhamento> =
        try {
            collection.whereEqualTo("usuarioId", usuarioId).get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(Acompanhamento::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }

    override suspend fun listarAcompanhamentoPorPaciente(pacienteId: String): List<Acompanhamento> =
        try {
            collection.whereEqualTo("pacienteId", pacienteId).get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(Acompanhamento::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }

    private companion object {
        const val COLLECTION = "Acompanhamento"
    }
}
