package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Calendar

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

    override fun observarRegistrosDoDia(
        pacienteId: String
    ): Flow<List<RegistroMedicacao>> = callbackFlow {
        val (inicio, fim) = intervaloDoDiaAtual()

        val registration = collection
            .whereEqualTo("pacienteId", pacienteId)
            .whereGreaterThanOrEqualTo("data", inicio)
            .whereLessThanOrEqualTo("data", fim)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val registros = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    doc.toObject(RegistroMedicacao::class.java)?.copy(id = doc.id)
                }
                trySend(registros)
            }

        awaitClose { registration.remove() }
    }

    private fun intervaloDoDiaAtual(): Pair<Timestamp, Timestamp> {
        val inicio = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val fim = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return Timestamp(inicio.time) to Timestamp(fim.time)
    }

    private companion object {
        const val COLLECTION = "RegistroMedicacao"
    }
}
