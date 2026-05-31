package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Paciente(
    @get:Exclude var id: String? = null,
    var nome: String = "",
    var dataNascimento: Timestamp? = null,
    var usuarioId: String = ""
)
