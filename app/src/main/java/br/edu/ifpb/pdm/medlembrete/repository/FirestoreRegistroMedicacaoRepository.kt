package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class FirestoreRegistroMedicacaoRepository(
    private val firestore: FirebaseFirestore = Firebase.firestore
) : RegistroMedicacaoRepository {

    private val collection = firestore.collection(COLLECTION)
    private val formatoDataBr = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

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
        val registration = try {
            val (inicio, fim) = intervaloDoDiaAtual()
            collection
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
        } catch (e: Exception) {
            close(e)
            return@callbackFlow
        }

        awaitClose { registration.remove() }
    }

    override suspend fun listarTodosRegistrosComFiltro(
        pacienteId: String,
        nomeMedicamento: String?,
        data: String?,
        usuarioId: String?
    ): List<RegistroMedicacao> = try {
        // Exige índice composto (pacienteId ASC, data DESC) — link aparece no log na 1a chamada.
        val todos = collection
            .whereEqualTo("pacienteId", pacienteId)
            .orderBy("data", Query.Direction.DESCENDING)
            .get().await()
            .documents.mapNotNull { doc ->
                doc.toObject(RegistroMedicacao::class.java)?.copy(id = doc.id)
            }

        var resultado = todos

        if (!usuarioId.isNullOrBlank()) {
            resultado = resultado.filter { it.usuarioId == usuarioId }
        }

        if (!data.isNullOrBlank()) {
            val intervalo = parseIntervaloDoDia(data)
            if (intervalo != null) {
                val (inicio, fim) = intervalo
                resultado = resultado.filter { reg ->
                    val regData = reg.data?.toDate() ?: return@filter false
                    !regData.before(inicio) && !regData.after(fim)
                }
            }
        }

        if (!nomeMedicamento.isNullOrBlank()) {
            val termo = nomeMedicamento.trim()
            val medicamentosDoPaciente = firestore.collection(MEDICAMENTO_COLLECTION)
                .whereEqualTo("pacienteId", pacienteId)
                .get().await()
                .documents.mapNotNull { doc ->
                    doc.toObject(Medicamento::class.java)?.copy(id = doc.id)
                }
            val idsQueBatem = medicamentosDoPaciente
                .filter { it.nome.contains(termo, ignoreCase = true) }
                .mapNotNull { it.id }
                .toSet()
            resultado = resultado.filter { it.medicamentoId in idsQueBatem }
        }

        resultado
    } catch (e: Exception) {
        emptyList()
    }

    private fun parseIntervaloDoDia(data: String): Pair<Date, Date>? = try {
        val parsed = formatoDataBr.parse(data) ?: return null
        val cal = Calendar.getInstance().apply {
            time = parsed
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val inicio = cal.time
        cal.apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        val fim = cal.time
        inicio to fim
    } catch (e: Exception) {
        null
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
        const val MEDICAMENTO_COLLECTION = "Medicamento"
    }
}
