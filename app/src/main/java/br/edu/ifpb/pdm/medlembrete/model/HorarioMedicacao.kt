package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class HorarioMedicacao(
    @get:Exclude var id: String? = null,
    var medicamentoId: String = "",
    var horario: String = ""
)
