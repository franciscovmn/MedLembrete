package br.edu.ifpb.pdm.medlembrete.network

import retrofit2.http.GET
import retrofit2.http.Query

interface OpenFdaApi {

    @GET("drug/label.json")
    suspend fun buscarLabel(
        @Query("search") search: String,
        @Query("limit") limit: Int = 1
    ): OpenFdaResponseDto
}
