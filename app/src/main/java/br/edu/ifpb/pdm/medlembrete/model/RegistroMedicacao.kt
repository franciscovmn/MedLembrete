package br.edu.ifpb.pdm.medlembrete.model

import br.edu.ifpb.pdm.medlembrete.enums.StatusMedicacao
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class RegistroMedicacao(
    @DocumentId var id: String? = null,
    var pacienteId: String = "",
    var medicamentoId: String = "",
    var usuarioId: String = "",
    var data: Timestamp? = null,
    var horarioConfirmacao: String = "",
    var status: StatusMedicacao = StatusMedicacao.PENDENTE
)