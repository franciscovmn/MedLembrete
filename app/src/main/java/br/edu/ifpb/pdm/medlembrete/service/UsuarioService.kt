package br.edu.ifpb.pdm.medlembrete.service

import br.edu.ifpb.pdm.medlembrete.model.Usuario
import br.edu.ifpb.pdm.medlembrete.repository.UsuarioRepository

class UsuarioService (private val usuarioRepository: UsuarioRepository) {
    suspend fun listarUsuarios(): List<Usuario> {
        return usuarioRepository.listarUsuarios()
    }

    suspend fun buscarUsuarioPorId(usuarioId: String): Usuario {
        return usuarioRepository.buscarUsuarioPorId(usuarioId)
    }

    suspend fun criarUsuario(
        nome: String,
        email: String,
        senha: String
    ): Usuario {
        val usuario = Usuario(
            nome = nome,
            email = email,
            senha = senha
        )

        return usuarioRepository.salvarUsuario(usuario)
    }

    suspend fun editarUsuario(
        usuarioId: String,
        nome: String,
        email: String,
        senha: String
    ): Usuario {
        val usuarioAtualizado = Usuario(
            id = usuarioId,
            nome = nome,
            email = email,
            senha = senha
        )

        return usuarioRepository.editarUsuario(usuarioAtualizado)
    }

    suspend fun excluirUsuario(usuario: Usuario) {
        usuarioRepository.excluirUsuario(usuario)
    }
}