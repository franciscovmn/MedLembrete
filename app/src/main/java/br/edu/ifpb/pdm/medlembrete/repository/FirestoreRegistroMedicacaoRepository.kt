package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirestoreRegistroMedicacaoRepository(
    firestore: FirebaseFirestore = Firebase.firestore
) : RegistroMedicacaoRepository {

    private val collection = firestore.collection(COLLECTION)

    override suspend fun listarRegistros(): List<RegistroMedicacao> = try {
        collection.get().await().documents.mapNotNull { doc ->
            doc.toObject(RegistroMedicacao::class.java)?.copy(id = doc.id)
        }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun listarRegistrosPorPaciente(pacienteId: String): List<RegistroMedicacao> =
        try {
            collection.whereEqualTo("pacienteId", pacienteId).get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(RegistroMedicacao::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }

    override suspend fun listarRegistrosPorUsuario(usuarioId: String): List<RegistroMedicacao> =
        try {
            collection.whereEqualTo("usuarioId", usuarioId).get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(RegistroMedicacao::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList()
        }

    override suspend fun listarRegistrosPorMedicamento(
        medicamentoId: String
    ): List<RegistroMedicacao> = try {
        collection.whereEqualTo("medicamentoId", medicamentoId).get().await()
            .documents.mapNotNull { doc ->
                doc.toObject(RegistroMedicacao::class.java)?.copy(id = doc.id)
            }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun salvarRegistro(registro: RegistroMedicacao): RegistroMedicacao {
        val ref = collection.add(registro).await()
        return registro.copy(id = ref.id)
    }

    override suspend fun atualizarRegistro(registro: RegistroMedicacao): RegistroMedicacao {
        val id = requireNotNull(registro.id) { "Registro sem ID não pode ser atualizado." }
        collection.document(id).set(registro).await()
        return registro
    }

    override suspend fun excluirRegistro(registro: RegistroMedicacao) {
        val id = requireNotNull(registro.id) { "Registro sem ID não pode ser excluído." }
        collection.document(id).delete().await()
    }

    private companion object {
        const val COLLECTION = "RegistroMedicacao"
    }
}
