package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.security.Timestamp

@IgnoreExtraProperties
data class Paciente (
    @DocumentId var id: String? = null,
    var nome: String = "",
    var dataNascimento: Timestamp? = null
)