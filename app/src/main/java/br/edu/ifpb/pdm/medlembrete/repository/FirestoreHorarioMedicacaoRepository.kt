package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirestoreHorarioMedicacaoRepository(
    firestore: FirebaseFirestore = Firebase.firestore
) : HorarioMedicacaoRepository {

    private val collection = firestore.collection(COLLECTION)

    override suspend fun listarHorariosMedicacaoPorMedicamento(): List<HorarioMedicacao> = try {
        collection.get().await().documents.mapNotNull { doc ->
            doc.toObject(HorarioMedicacao::class.java)?.copy(id = doc.id)
        }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun listarPorMedicamento(medicamentoId: String): List<HorarioMedicacao> = try {
        collection.whereEqualTo("medicamentoId", medicamentoId).get().await()
            .documents.mapNotNull { doc ->
                doc.toObject(HorarioMedicacao::class.java)?.copy(id = doc.id)
            }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun salvarHorario(horario: HorarioMedicacao): HorarioMedicacao {
        val ref = collection.add(horario).await()
        return horario.copy(id = ref.id)
    }

    override suspend fun editarHorario(horario: HorarioMedicacao): HorarioMedicacao {
        val id = requireNotNull(horario.id) { "Horário sem ID não pode ser editado." }
        collection.document(id).set(horario).await()
        return horario
    }

    override suspend fun excluirHorario(horario: HorarioMedicacao) {
        val id = requireNotNull(horario.id) { "Horário sem ID não pode ser excluído." }
        collection.document(id).delete().await()
    }

    private companion object {
        const val COLLECTION = "HorarioMedicacao"
    }
}
