package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.Usuario
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class FirestoreUsuarioRepository(
    firestore: FirebaseFirestore = Firebase.firestore
) : UsuarioRepository {

    private val collection = firestore.collection(COLLECTION)

    override suspend fun listarUsuarios(): List<Usuario> = try {
        collection.get().await().documents.mapNotNull { doc ->
            doc.toObject(Usuario::class.java)?.copy(id = doc.id)
        }
    } catch (e: Exception) {
        emptyList()
    }

    override suspend fun buscarUsuarioPorId(usuarioId: String): Usuario {
        val doc = collection.document(usuarioId).get().await()
        val usuario = doc.toObject(Usuario::class.java)?.copy(id = doc.id)
        if (usuario != null && usuario.nome.isNotBlank()) return usuario

        // fallback: busca por similaridade (tolera zero vs O, case)
        return collection
            .get().await()
            .documents
            .mapNotNull { it.toObject(Usuario::class.java)?.copy(id = it.id) }
            .firstOrNull { it.id.equals(usuarioId, ignoreCase = true) }
            ?: Usuario(id = usuarioId, nome = "Cuidador")
    }

    override suspend fun salvarUsuario(usuario: Usuario): Usuario {
        val ref = collection.add(usuario).await()
        return usuario.copy(id = ref.id)
    }

    override suspend fun editarUsuario(usuario: Usuario): Usuario {
        val id = requireNotNull(usuario.id) { "Usuário sem ID não pode ser editado." }
        collection.document(id).set(usuario).await()
        return usuario
    }

    override suspend fun excluirUsuario(usuario: Usuario) {
        val id = requireNotNull(usuario.id) { "Usuário sem ID não pode ser excluído." }
        collection.document(id).delete().await()
    }

    private companion object {
        const val COLLECTION = "Usuario"
    }
}
