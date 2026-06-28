package br.edu.ifpb.pdm.medlembrete.repository.local

import br.edu.ifpb.pdm.medlembrete.model.Medicamento

/** Converte o modelo de domínio para a entidade do Room (cache local). */
fun Medicamento.toEntity(): MedicamentoEntity = MedicamentoEntity(
    id = requireNotNull(id) { "Medicamento sem id não pode ser salvo no cache local." },
    nome = nome,
    nomeEn = nomeEn,
    dosagem = dosagem,
    instrucoesUso = instrucoesUso,
    pacienteId = pacienteId
)

/** Converte a entidade do Room de volta para o modelo de domínio. */
fun MedicamentoEntity.toDomain(): Medicamento = Medicamento(
    id = id,
    nome = nome,
    nomeEn = nomeEn,
    dosagem = dosagem,
    instrucoesUso = instrucoesUso,
    pacienteId = pacienteId
)
