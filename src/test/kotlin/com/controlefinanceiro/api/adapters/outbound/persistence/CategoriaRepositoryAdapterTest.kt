package com.controlefinanceiro.api.adapters.outbound.persistence

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.CategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.CategoriaJpaRepository
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.SubcategoriaJpaRepository
import com.controlefinanceiro.api.exception.NotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

class CategoriaRepositoryAdapterTest {

    private val repo: CategoriaJpaRepository = Mockito.mock(CategoriaJpaRepository::class.java)
    private val subRepo: SubcategoriaJpaRepository = Mockito.mock(SubcategoriaJpaRepository::class.java)
    private val adapter = CategoriaRepositoryAdapter(repo, subRepo)

    @Test
    fun `create deve salvar e retornar entity`() {
        val saved = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        Mockito.`when`(repo.save(Mockito.any(CategoriaPersistenceEntity::class.java))).thenReturn(saved)

        val result = adapter.create("Transporte")

        assertEquals(1L, result.idCategoria)
        assertEquals("Transporte", result.nome)
        Mockito.verify(repo).save(Mockito.any(CategoriaPersistenceEntity::class.java))
    }

    @Test
    fun `update deve alterar nome e salvar`() {
        val existing = CategoriaPersistenceEntity(idCategoria = 10L, nome = "Old")
        Mockito.`when`(repo.findById(10L)).thenReturn(Optional.of(existing))
        Mockito.`when`(repo.save(existing)).thenReturn(existing.apply { nome = "Novo" })

        val result = adapter.update(10L, "Novo")

        assertEquals(10L, result.idCategoria)
        assertEquals("Novo", result.nome)
        Mockito.verify(repo).findById(10L)
        Mockito.verify(repo).save(existing)
    }

    @Test
    fun `update deve lançar NotFound quando categoria não existe`() {
        Mockito.`when`(repo.findById(99L)).thenReturn(Optional.empty())

        assertThrows(NotFoundException::class.java) {
            adapter.update(99L, "X")
        }
        Mockito.verify(repo).findById(99L)
    }

    @Test
    fun `delete deve apagar subcategorias e depois categoria`() {
        Mockito.`when`(subRepo.findAllByCategoria_IdCategoria(5L)).thenReturn(emptyList())
        Mockito.doNothing().`when`(subRepo).deleteAll(Mockito.anyList())
        Mockito.doNothing().`when`(repo).deleteById(5L)

        adapter.delete(5L)

        Mockito.verify(subRepo).findAllByCategoria_IdCategoria(5L)
        Mockito.verify(subRepo).deleteAll(Mockito.anyList())
        Mockito.verify(repo).deleteById(5L)
    }

    @Test
    fun `findById deve mapear para domain`() {
        val existing = CategoriaPersistenceEntity(idCategoria = 7L, nome = "Casa")
        Mockito.`when`(repo.findById(7L)).thenReturn(Optional.of(existing))

        val result = adapter.findById(7L)

        assertNotNull(result)
        assertEquals(7L, result!!.idCategoria)
        assertEquals("Casa", result.nome)
    }

    @Test
    fun `search sem nome deve buscar tudo`() {
        val list = listOf(
            CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte"),
            CategoriaPersistenceEntity(idCategoria = 2L, nome = "Alimentação")
        )
        Mockito.`when`(repo.findAll()).thenReturn(list)

        val result = adapter.search(null)

        assertEquals(2, result.size)
        Mockito.verify(repo).findAll()
        Mockito.verify(repo, Mockito.never()).findAllByNomeContainingIgnoreCase(Mockito.anyString())
    }

    @Test
    fun `search com nome deve buscar por like ignore case`() {
        val list = listOf(CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte"))
        Mockito.`when`(repo.findAllByNomeContainingIgnoreCase("tra")).thenReturn(list)

        val result = adapter.search("tra")

        assertEquals(1, result.size)
        assertEquals("Transporte", result[0].nome)
        Mockito.verify(repo).findAllByNomeContainingIgnoreCase("tra")
        Mockito.verify(repo, Mockito.never()).findAll()
    }

    @Test
    fun `existsByNome deve delegar`() {
        Mockito.`when`(repo.existsByNome("Transporte")).thenReturn(true)

        assertTrue(adapter.existsByNome("Transporte"))
        Mockito.verify(repo).existsByNome("Transporte")
    }
}
