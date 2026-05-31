package br.edu.ifpb.pdm.medlembrete.network

import com.google.gson.annotations.SerializedName

data class OpenFdaResponseDto(
    @SerializedName("results")
    val results: List<DrugLabelDto>?
)

data class DrugLabelDto(
    @SerializedName("openfda")
    val openfda: OpenFdaInfoDto?,

    @SerializedName("indications_and_usage")
    val indicationsAndUsage: List<String>?,

    @SerializedName("warnings")
    val warnings: List<String>?,

    @SerializedName("dosage_and_administration")
    val dosageAndAdministration: List<String>?
)

data class OpenFdaInfoDto(
    @SerializedName("brand_name")
    val brandName: List<String>?,

    @SerializedName("generic_name")
    val genericName: List<String>?
)
