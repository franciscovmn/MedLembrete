package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Paciente (
    @DocumentId var id: String? = null,
    var nome: String = "",
    var dataNascimento: Timestamp? = null
)