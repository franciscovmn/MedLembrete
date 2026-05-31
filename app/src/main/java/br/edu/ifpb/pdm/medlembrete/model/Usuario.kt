package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Usuario(
    @get:Exclude var id: String? = null,
    var nome: String = "",
    var email: String = "",
    var senha: String = ""
)
