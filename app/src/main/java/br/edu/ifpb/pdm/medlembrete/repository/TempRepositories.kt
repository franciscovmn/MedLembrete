package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.Acompanhamento
import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.model.Paciente
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Usuario

/*
    Aqui entra a parte do Felipe:
    ele ficou responsável por criar os repositories,
    que fazem a comunicação com a API usando as classes do Retrofit.

    por enquanto, essas interfaces só retornam coisas vazias.
*/

interface UsuarioRepository {
    suspend fun listarUsuarios(): List<Usuario>
    suspend fun buscarUsuarioPorId(usuarioId: String): Usuario

    suspend fun salvarUsuario(usuario: Usuario): Usuario
    suspend fun editarUsuario(usuario: Usuario): Usuario
    suspend fun excluirUsuario(usuario: Usuario)
}

interface PacienteRepository {
    suspend fun listarPacientes(): List<Paciente>

    suspend fun listarPacientesPorUsuario(usuarioId: String): List<Paciente>

    suspend fun buscarPacientePorId(pacienteId: String): Paciente

    suspend fun salvarPaciente(paciente: Paciente): Paciente

    suspend fun atualizarPaciente(paciente: Paciente): Paciente

    suspend fun excluirPaciente(paciente: Paciente)
}

interface MedicamentoRepository {
    suspend fun listarMedicamentos(): List<Medicamento> = emptyList()
    suspend fun listarPorPacienteId(pacienteId: String): List<Medicamento>

    suspend fun salvarMedicamento(medicamento: Medicamento)

    suspend fun atualizarMedicamento(medicamento: Medicamento)

    suspend fun excluirMedicamento(medicamentoId: String)
}

interface RegistroMedicacaoRepository {
    suspend fun listarRegistros(): List<RegistroMedicacao> = emptyList()

    suspend fun listarRegistrosPorPaciente(pacienteId: String): List<RegistroMedicacao> = emptyList()

    suspend fun listarRegistrosPorUsuario(usuarioId: String): List<RegistroMedicacao> = emptyList()

    suspend fun listarRegistrosPorMedicamento(medicamentoId: String): List<RegistroMedicacao> = emptyList()

    suspend fun salvarRegistro(registro: RegistroMedicacao): RegistroMedicacao

    suspend fun atualizarRegistro(registro: RegistroMedicacao): RegistroMedicacao

    suspend fun excluirRegistro(registro: RegistroMedicacao)
}

interface HorarioMedicacaoRepository {
    suspend fun listarHorariosMedicacaoPorMedicamento(): List<HorarioMedicacao>
    suspend fun listarPorMedicamento(medicamentoId: String): List<HorarioMedicacao>

    suspend fun salvarHorario(horario: HorarioMedicacao): HorarioMedicacao
    suspend fun editarHorario(horario: HorarioMedicacao): HorarioMedicacao
    suspend fun excluirHorario(horario: HorarioMedicacao)
}

interface AcompanhamentoRepository {
    suspend fun salvarAcompanhamento(acompanhamento: Acompanhamento): Acompanhamento
    suspend fun excluirAcompanhamento(acompanhamento: Acompanhamento)

    suspend fun listarAcompanhamentoPorUsuario(usuarioId: String): List<Acompanhamento>
    suspend fun listarAcompanhamentoPorPaciente(pacienteId: String): List<Acompanhamento>
}