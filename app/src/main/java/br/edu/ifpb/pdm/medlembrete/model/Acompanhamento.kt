package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Acompanhamento(
    @get:Exclude var id: String? = null,
    var usuarioId: String = "",
    var pacienteId: String = ""
)
