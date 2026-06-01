package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.enums.StatusMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Acompanhamento
import br.edu.ifpb.pdm.medlembrete.model.HorarioMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Medicamento
import br.edu.ifpb.pdm.medlembrete.model.Paciente
import br.edu.ifpb.pdm.medlembrete.model.RegistroMedicacao
import br.edu.ifpb.pdm.medlembrete.model.Usuario
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object MockDatabase {
    val usuarios = mutableListOf(
        Usuario(
            id = "user1",
            nome = "Maria Silva",
            email = "maria@email.com",
            senha = "123456"
        ),
        Usuario(
            id = "user2",
            nome = "João Santos",
            email = "joao@email.com",
            senha = "123456"
        )
    )

    val pacientes = mutableListOf(
        Paciente(
            id = "pac1",
            nome = "José da Silva"
        ),
        Paciente(
            id = "pac2",
            nome = "Ana Souza"
        )
    )

    val acompanhamentos = mutableListOf(
        Acompanhamento(
            id = "acomp1",
            usuarioId = "user1",
            pacienteId = "pac1"
        ),
        Acompanhamento(
            id = "acomp2",
            usuarioId = "user1",
            pacienteId = "pac2"
        ),
        Acompanhamento(
            id = "acomp3",
            usuarioId = "user2",
            pacienteId = "pac2"
        )
    )

    val medicamentos = mutableListOf(
        Medicamento(
            id = "med1",
            nome = "Losartana",
            dosagem = "50mg",
            instrucoesUso = "Tomar após o café",
            pacienteId = "pac1"
        ),
        Medicamento(
            id = "med2",
            nome = "Dipirona",
            dosagem = "500mg",
            instrucoesUso = "Se houver dor",
            pacienteId = "pac1"
        ),
        Medicamento(
            id = "med3",
            nome = "Metformina",
            dosagem = "850mg",
            instrucoesUso = "Após almoço",
            pacienteId = "pac2"
        )
    )

    val horarios = mutableListOf(
        HorarioMedicacao(
            id = "hor1",
            medicamentoId = "med1",
            horario = "08:00"
        ),
        HorarioMedicacao(
            id = "hor2",
            medicamentoId = "med1",
            horario = "20:00"
        ),
        HorarioMedicacao(
            id = "hor3",
            medicamentoId = "med3",
            horario = "12:00"
        )
    )

    val registros = mutableListOf(
        RegistroMedicacao(
            id = "reg1",
            pacienteId = "pac1",
            medicamentoId = "med1",
            usuarioId = "user1",
            horarioConfirmacao = "08:00",
            status = StatusMedicacao.TOMADO
        ),
        RegistroMedicacao(
            id = "reg2",
            pacienteId = "pac1",
            medicamentoId = "med2",
            usuarioId = "user1",
            horarioConfirmacao = "",
            status = StatusMedicacao.PENDENTE
        )
    )

}

class MockUsuarioRepository : UsuarioRepository {
    override suspend fun listarUsuarios() =
        MockDatabase.usuarios.toList()

    override suspend fun buscarUsuarioPorId(usuarioId: String) =
        MockDatabase.usuarios.first { it.id == usuarioId }

    override suspend fun salvarUsuario(usuario: Usuario): Usuario {
        val novo = usuario.copy(
            id = usuario.id ?: "user${MockDatabase.usuarios.size + 1}"
        )

        MockDatabase.usuarios.add(novo)
        return novo
    }

    override suspend fun editarUsuario(usuario: Usuario): Usuario {
        val index = MockDatabase.usuarios.indexOfFirst {
            it.id == usuario.id
        }

        if (index >= 0) {
            MockDatabase.usuarios[index] = usuario
        }

        return usuario
    }

    override suspend fun excluirUsuario(usuario: Usuario) {
        MockDatabase.usuarios.removeIf {
            it.id == usuario.id
        }
    }
    

}

class MockPacienteRepository : PacienteRepository {
    override suspend fun listarPacientes() =
        MockDatabase.pacientes.toList()

    override suspend fun listarPacientesPorUsuario(
        usuarioId: String
    ): List<Paciente> {

        val pacientesIds = MockDatabase.acompanhamentos
            .filter { it.usuarioId == usuarioId }
            .map { it.pacienteId }

        return MockDatabase.pacientes.filter {
            it.id in pacientesIds
        }
    }

    override suspend fun buscarPacientePorId(
        pacienteId: String
    ): Paciente {
        return MockDatabase.pacientes.first {
            it.id == pacienteId
        }
    }

    override suspend fun salvarPaciente(
        paciente: Paciente
    ): Paciente {

        val novo = paciente.copy(
            id = paciente.id ?: "pac${MockDatabase.pacientes.size + 1}"
        )

        MockDatabase.pacientes.add(novo)

        return novo
    }

    override suspend fun atualizarPaciente(
        paciente: Paciente
    ): Paciente {

        val index = MockDatabase.pacientes.indexOfFirst {
            it.id == paciente.id
        }

        if (index >= 0) {
            MockDatabase.pacientes[index] = paciente
        }

        return paciente
    }

    override suspend fun excluirPaciente(
        paciente: Paciente
    ) {
        MockDatabase.pacientes.removeIf {
            it.id == paciente.id
        }
    }
    

}

class MockMedicamentoRepository : MedicamentoRepository {

    
    override suspend fun listarMedicamentos() =
        MockDatabase.medicamentos.toList()

    override suspend fun buscarMedicamentoPorID(
        medicamentoId: String
    ): Medicamento {
        return MockDatabase.medicamentos.first {
            it.id == medicamentoId
        }
    }

    override suspend fun listarPorPacienteId(
        pacienteId: String
    ): List<Medicamento> {

        return MockDatabase.medicamentos.filter {
            it.pacienteId == pacienteId
        }
    }

    override suspend fun salvarMedicamento(
        medicamento: Medicamento
    ): Medicamento {
        MockDatabase.medicamentos.add(
            medicamento.copy(
                id = "med${MockDatabase.medicamentos.size + 1}"
            )
        )

        return medicamento
    }

    override suspend fun atualizarMedicamento(
        medicamento: Medicamento
    ): Medicamento {
        val index = MockDatabase.medicamentos.indexOfFirst {
            it.id == medicamento.id
        }

        if (index >= 0) {
            MockDatabase.medicamentos[index] = medicamento
        }

        return medicamento
    }

    override suspend fun excluirMedicamento(
        medicamentoId: String
    ) {
        MockDatabase.medicamentos.removeIf {
            it.id == medicamentoId
        }
    }
    

}

class MockRegistroMedicacaoRepository :
    RegistroMedicacaoRepository {

    
    override suspend fun listarRegistros() =
        MockDatabase.registros.toList()

    override suspend fun listarRegistrosPorPaciente(
        pacienteId: String
    ) = MockDatabase.registros.filter {
        it.pacienteId == pacienteId
    }

    override suspend fun listarRegistrosPorUsuario(
        usuarioId: String
    ) = MockDatabase.registros.filter {
        it.usuarioId == usuarioId
    }

    override suspend fun listarRegistrosPorMedicamento(
        medicamentoId: String
    ) = MockDatabase.registros.filter {
        it.medicamentoId == medicamentoId
    }

    override suspend fun salvarRegistro(
        registro: RegistroMedicacao
    ): RegistroMedicacao {

        val novo = registro.copy(
            id = "reg${MockDatabase.registros.size + 1}"
        )

        MockDatabase.registros.add(novo)

        return novo
    }

    override suspend fun atualizarRegistro(
        registro: RegistroMedicacao
    ): RegistroMedicacao {

        val index = MockDatabase.registros.indexOfFirst {
            it.id == registro.id
        }

        if (index >= 0) {
            MockDatabase.registros[index] = registro
        }

        return registro
    }

    override suspend fun excluirRegistro(
        registro: RegistroMedicacao
    ) {
        MockDatabase.registros.removeIf {
            it.id == registro.id
        }
    }

    override fun observarRegistrosDoDia(
        pacienteId: String
    ): Flow<List<RegistroMedicacao>> = flow {
        emit(listarRegistrosPorPaciente(pacienteId))
    }

    override suspend fun listarTodosRegistrosComFiltro(
        pacienteId: String,
        nomeMedicamento: String?,
        data: String?,
        usuarioId: String?
    ): List<RegistroMedicacao> = listarRegistrosPorPaciente(pacienteId)


}

class MockHorarioMedicacaoRepository :
    HorarioMedicacaoRepository {

    
    override suspend fun listarHorariosMedicacaoPorMedicamento() =
        MockDatabase.horarios.toList()

    override suspend fun listarPorMedicamento(
        medicamentoId: String
    ) = MockDatabase.horarios.filter {
        it.medicamentoId == medicamentoId
    }

    override suspend fun salvarHorario(
        horario: HorarioMedicacao
    ): HorarioMedicacao {

        val novo = horario.copy(
            id = "hor${MockDatabase.horarios.size + 1}"
        )

        MockDatabase.horarios.add(novo)

        return novo
    }

    override suspend fun editarHorario(
        horario: HorarioMedicacao
    ): HorarioMedicacao {

        val index = MockDatabase.horarios.indexOfFirst {
            it.id == horario.id
        }

        if (index >= 0) {
            MockDatabase.horarios[index] = horario
        }

        return horario
    }

    override suspend fun excluirHorario(
        horario: HorarioMedicacao
    ) {
        MockDatabase.horarios.removeIf {
            it.id == horario.id
        }
    }
    

}

class MockAcompanhamentoRepository :
    AcompanhamentoRepository {

    
    override suspend fun salvarAcompanhamento(
        acompanhamento: Acompanhamento
    ): Acompanhamento {

        val novo = acompanhamento.copy(
            id = "acomp${MockDatabase.acompanhamentos.size + 1}"
        )

        MockDatabase.acompanhamentos.add(novo)

        return novo
    }

    override suspend fun excluirAcompanhamento(
        acompanhamento: Acompanhamento
    ) {
        MockDatabase.acompanhamentos.removeIf {
            it.id == acompanhamento.id
        }
    }

    override suspend fun listarAcompanhamentoPorUsuario(
        usuarioId: String
    ) = MockDatabase.acompanhamentos.filter {
        it.usuarioId == usuarioId
    }

    override suspend fun listarAcompanhamentoPorPaciente(
        pacienteId: String
    ) = MockDatabase.acompanhamentos.filter {
        it.pacienteId == pacienteId
    }

}
