package br.edu.ifpb.pdm.medlembrete.repository

import br.edu.ifpb.pdm.medlembrete.model.MedicamentoInfoExterna
import br.edu.ifpb.pdm.medlembrete.network.DrugLabelDto
import br.edu.ifpb.pdm.medlembrete.network.OpenFdaApi
import br.edu.ifpb.pdm.medlembrete.network.RetrofitClient

class OpenFdaRepository(
    private val api: OpenFdaApi = RetrofitClient.openFdaApi
) {

    suspend fun buscarInfoMedicamento(nome: String): Result<MedicamentoInfoExterna> =
        runCatching {
            val termo = nome.trim()
            require(termo.isNotEmpty()) { "Nome do medicamento não pode ser vazio." }

            val query = "openfda.brand_name:\"$termo\"+openfda.generic_name:\"$termo\""
            val resposta = api.buscarLabel(search = query, limit = 1)

            val primeiro = resposta.results?.firstOrNull()
                ?: error("Nenhuma informação encontrada na OpenFDA para \"$termo\".")

            primeiro.toDomain()
        }

    private fun DrugLabelDto.toDomain(): MedicamentoInfoExterna = MedicamentoInfoExterna(
        brandName = openfda?.brandName?.firstOrNull(),
        genericName = openfda?.genericName?.firstOrNull(),
        indicacoes = indicationsAndUsage?.joinToString(separator = "\n\n"),
        avisos = warnings?.joinToString(separator = "\n\n"),
        posologia = dosageAndAdministration?.joinToString(separator = "\n\n")
    )
}
