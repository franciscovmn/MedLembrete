package br.edu.ifpb.pdm.medlembrete.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class HorarioMedicacao(
    @DocumentId var id: String? = null,
    var medicamentoId: String = "",
    var horario: String = ""
)
