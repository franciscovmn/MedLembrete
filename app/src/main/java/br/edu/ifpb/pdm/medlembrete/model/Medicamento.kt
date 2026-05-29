package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Medicamento (
    @DocumentId var id:String = "",
    var nome:String = "",
    var dosagem: String = "",
    var instrucoesUso: String = "",
    var pacienteId: String = ""
)