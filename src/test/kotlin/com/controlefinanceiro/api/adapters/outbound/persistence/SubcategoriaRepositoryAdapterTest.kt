package com.controlefinanceiro.api.adapters.outbound.persistence

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.CategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.entity.SubcategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.CategoriaJpaRepository
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.SubcategoriaJpaRepository
import com.controlefinanceiro.api.exception.NotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

class SubcategoriaRepositoryAdapterTest {

    private val repo: SubcategoriaJpaRepository = Mockito.mock(SubcategoriaJpaRepository::class.java)
    private val categoriaRepo: CategoriaJpaRepository = Mockito.mock(CategoriaJpaRepository::class.java)
    private val adapter = SubcategoriaRepositoryAdapter(repo, categoriaRepo)

    @Test
    fun `create deve salvar e retornar entity`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        Mockito.`when`(categoriaRepo.findById(1L)).thenReturn(Optional.of(cat))

        val saved = SubcategoriaPersistenceEntity(idSubcategoria = 10L, nome = "Uber", categoria = cat)
        Mockito.`when`(repo.save(Mockito.any(SubcategoriaPersistenceEntity::class.java))).thenReturn(saved)

        val result = adapter.create("Uber", 1L)

        assertEquals(10L, result.idSubcategoria)
        assertEquals("Uber", result.nome)
        assertEquals(1L, result.idCategoria)
        Mockito.verify(categoriaRepo).findById(1L)
        Mockito.verify(repo).save(Mockito.any(SubcategoriaPersistenceEntity::class.java))
    }

    @Test
    fun `update deve alterar e salvar`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 2L, nome = "Casa")
        val existing = SubcategoriaPersistenceEntity(idSubcategoria = 3L, nome = "Antigo", categoria = cat)

        Mockito.`when`(repo.findById(3L)).thenReturn(Optional.of(existing))
        Mockito.`when`(categoriaRepo.findById(2L)).thenReturn(Optional.of(cat))
        Mockito.`when`(repo.save(existing)).thenReturn(existing.apply { nome = "Novo" })

        val result = adapter.update(3L, "Novo", 2L)

        assertEquals(3L, result.idSubcategoria)
        assertEquals("Novo", result.nome)
        assertEquals(2L, result.idCategoria)
        Mockito.verify(repo).findById(3L)
        Mockito.verify(categoriaRepo).findById(2L)
        Mockito.verify(repo).save(existing)
    }

    @Test
    fun `update deve lançar NotFound quando subcategoria não existe`() {
        Mockito.`when`(repo.findById(999L)).thenReturn(Optional.empty())

        assertThrows(NotFoundException::class.java) {
            adapter.update(999L, "X", 1L)
        }
        Mockito.verify(repo).findById(999L)
    }

    @Test
    fun `delete deve apagar por id`() {
        Mockito.doNothing().`when`(repo).deleteById(7L)

        adapter.delete(7L)

        Mockito.verify(repo).deleteById(7L)
    }

    @Test
    fun `findById deve mapear`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val existing = SubcategoriaPersistenceEntity(idSubcategoria = 11L, nome = "Uber", categoria = cat)
        Mockito.`when`(repo.findById(11L)).thenReturn(Optional.of(existing))

        val result = adapter.findById(11L)

        assertNotNull(result)
        assertEquals(11L, result!!.idSubcategoria)
        assertEquals("Uber", result.nome)
        assertEquals(1L, result.idCategoria)
    }

    @Test
    fun `existsByNomeInCategoria deve delegar`() {
        Mockito.`when`(repo.existsByNomeAndCategoria_IdCategoria("Uber", 1L)).thenReturn(true)

        assertTrue(adapter.existsByNomeInCategoria("Uber", 1L))
        Mockito.verify(repo).existsByNomeAndCategoria_IdCategoria("Uber", 1L)
    }

    @Test
    fun `findAllByCategoria deve mapear`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val list = listOf(SubcategoriaPersistenceEntity(idSubcategoria = 3L, nome = "Uber", categoria = cat))
        Mockito.`when`(repo.findAllByCategoria_IdCategoria(1L)).thenReturn(list)

        val result = adapter.findAllByCategoria(1L)

        assertEquals(1, result.size)
        assertEquals(3L, result[0].idSubcategoria)
    }
}
