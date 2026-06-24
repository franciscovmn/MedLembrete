package br.edu.ifpb.pdm.medlembrete.di

import br.edu.ifpb.pdm.medlembrete.network.OpenFdaApi
import br.edu.ifpb.pdm.medlembrete.network.RetrofitClient
import br.edu.ifpb.pdm.medlembrete.repository.AcompanhamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.FirestoreAcompanhamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.FirestoreHorarioMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.FirestoreMedicamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.FirestorePacienteRepository
import br.edu.ifpb.pdm.medlembrete.repository.FirestoreRegistroMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.FirestoreUsuarioRepository
import br.edu.ifpb.pdm.medlembrete.repository.HorarioMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.MedicamentoRepository
import br.edu.ifpb.pdm.medlembrete.repository.OpenFdaRepository
import br.edu.ifpb.pdm.medlembrete.repository.PacienteRepository
import br.edu.ifpb.pdm.medlembrete.repository.RegistroMedicacaoRepository
import br.edu.ifpb.pdm.medlembrete.repository.UsuarioRepository
import br.edu.ifpb.pdm.medlembrete.service.AcompanhamentoService
import br.edu.ifpb.pdm.medlembrete.service.HorarioMedicacaoService
import br.edu.ifpb.pdm.medlembrete.service.MedicamentoService
import br.edu.ifpb.pdm.medlembrete.service.PacienteService
import br.edu.ifpb.pdm.medlembrete.service.RegistroMedicacaoService
import br.edu.ifpb.pdm.medlembrete.service.UsuarioService
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.CadastrarMedicamentoViewModel
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.DetalheViewModel
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.HistoricoViewModel
import br.edu.ifpb.pdm.medlembrete.ui.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // NETWORK
    single<OpenFdaApi> { RetrofitClient.openFdaApi }

    // OpenFDA
    single { OpenFdaRepository(get()) }

    // REPOSITORIES
    single<MedicamentoRepository> { FirestoreMedicamentoRepository() }
    single<HorarioMedicacaoRepository> { FirestoreHorarioMedicacaoRepository() }
    single<RegistroMedicacaoRepository> { FirestoreRegistroMedicacaoRepository() }
    single<PacienteRepository> { FirestorePacienteRepository() }
    single<UsuarioRepository> { FirestoreUsuarioRepository() }
    single<AcompanhamentoRepository> { FirestoreAcompanhamentoRepository() }

    // SERVICES
    single {
        MedicamentoService(
            medicamentoRepository = get(),
            registroRepository = get(),
            horarioRepository = get()
        )
    }

    single {
        HorarioMedicacaoService(
            horarioRepository = get()
        )
    }

    single {
        RegistroMedicacaoService(
            registroRepository = get()
        )
    }

    single {
        PacienteService(
            pacienteRepository = get()
        )
    }

    single {
        UsuarioService(
            usuarioRepository = get()
        )
    }

    single {
        AcompanhamentoService(
            acompanhamentoRepository = get()
        )
    }

    // VIEWMODELS
    viewModel {
        HomeViewModel(
            medicamentoRepository = get(),
            horarioRepository = get(),
            registroRepository = get(),
            usuarioRepository = get(),
            pacienteRepository = get()
        )
    }

    viewModel {
        HistoricoViewModel(
            registroRepository = get(),
            medicamentoRepository = get(),
            usuarioRepository = get()
        )
    }

    viewModel {
        DetalheViewModel(
            medicamentoRepository = get(),
            registroRepository = get(),
            usuarioRepository = get(),
            horarioRepository = get(),
            openFdaRepository = get()
        )
    }

    viewModel {
        CadastrarMedicamentoViewModel(
            medicamentoRepository = get(),
            horarioRepository = get()
        )
    }
}
