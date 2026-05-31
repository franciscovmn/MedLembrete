package br.edu.ifpb.pdm.medlembrete.repository

object RepositoryProvider {

    val usuarioRepository: UsuarioRepository by lazy {
        FirestoreUsuarioRepository()
    }

    val pacienteRepository: PacienteRepository by lazy {
        FirestorePacienteRepository()
    }

    val acompanhamentoRepository: AcompanhamentoRepository by lazy {
        FirestoreAcompanhamentoRepository()
    }

    val medicamentoRepository: MedicamentoRepository by lazy {
        FirestoreMedicamentoRepository()
    }

    val horarioMedicacaoRepository: HorarioMedicacaoRepository by lazy {
        FirestoreHorarioMedicacaoRepository()
    }

    val registroMedicacaoRepository: RegistroMedicacaoRepository by lazy {
        FirestoreRegistroMedicacaoRepository()
    }
}
