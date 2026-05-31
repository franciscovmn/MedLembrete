package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.Acompanhamento
import br.edu.ifpb.pdm.medlembrete.model.Paciente
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirestorePacienteRepository(
    private val firestore: FirebaseFirestore = Firebase.firestore
) : PacienteRepository {

    private val collection = firestore.collection(COLLECTION)

    override suspend fun listarPacientes(): List<Paciente> = try {
        collection.get().await().documents.mapNotNull { doc ->
            doc.toObject(Paciente::class.java)?.copy(id = doc.id)
        }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun listarPacientesPorUsuario(usuarioId: String): List<Paciente> = try {
        val pacientesIds = firestore.collection("Acompanhamento")
            .whereEqualTo("usuarioId", usuarioId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(Acompanhamento::class.java)?.pacienteId }
            .filter { it.isNotBlank() }

        if (pacientesIds.isEmpty()) {
            emptyList()
        } else {
            pacientesIds.distinct()
                .chunked(FIRESTORE_IN_LIMIT)
                .flatMap { chunk ->
                    collection.whereIn("__name__", chunk).get().await().documents
                }
                .mapNotNull { doc ->
                    doc.toObject(Paciente::class.java)?.copy(id = doc.id)
                }
        }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun buscarPacientePorId(pacienteId: String): Paciente {
        val doc = collection.document(pacienteId).get().await()
        val paciente = doc.toObject(Paciente::class.java)
            ?: error("Paciente '$pacienteId' não encontrado.")
        return paciente.copy(id = doc.id)
    }

    override suspend fun salvarPaciente(paciente: Paciente): Paciente {
        val ref = collection.add(paciente).await()
        return paciente.copy(id = ref.id)
    }

    override suspend fun atualizarPaciente(paciente: Paciente): Paciente {
        val id = requireNotNull(paciente.id) { "Paciente sem ID não pode ser atualizado." }
        collection.document(id).set(paciente).await()
        return paciente
    }

    override suspend fun excluirPaciente(paciente: Paciente) {
        val id = requireNotNull(paciente.id) { "Paciente sem ID não pode ser excluído." }
        collection.document(id).delete().await()
    }

    private companion object {
        const val COLLECTION = "Paciente"
        const val FIRESTORE_IN_LIMIT = 10
    }
}
