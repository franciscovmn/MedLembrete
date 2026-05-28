package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Acompanhamento(
    @DocumentId var id: String? = null,
    var usuarioId: String = "",
    var pacienteId: String = ""
)