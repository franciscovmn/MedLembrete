package br.edu.ifpb.pdm.medlembrete

import br.edu.ifpb.pdm.medlembrete.repository.OpenFdaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenFdaRepositoryTest {

    private val repository = OpenFdaRepository()

    @Test
    fun `busca real por ibuprofen retorna sucesso`() = runTest {
        val result = repository.buscarInfoMedicamento("ibuprofen")

        assertTrue("Resultado deveria ser sucesso: ${result.exceptionOrNull()}", result.isSuccess)
        val info = result.getOrNull()
        assertNotNull(info)
        println("Brand:   ${info?.brandName}")
        println("Generic: ${info?.genericName}")
        println("Indica:  ${info?.indicacoes?.take(120)}...")
    }

    @Test
    fun `nome vazio retorna falha`() = runTest {
        val result = repository.buscarInfoMedicamento("   ")
        assertTrue(result.isFailure)
    }
}
