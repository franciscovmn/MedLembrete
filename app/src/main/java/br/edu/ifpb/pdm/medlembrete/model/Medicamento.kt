package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Medicamento(
    @get:Exclude var id: String? = null,
    var nome: String = "",
    var dosagem: String = "",
    var instrucoesUso: String = "",
    var pacienteId: String = ""
)
