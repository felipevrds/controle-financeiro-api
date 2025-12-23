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
    fun `create deve buscar categoria, salvar e mapear`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 10L, nome = "Transporte")
        Mockito.`when`(categoriaRepo.findById(10L)).thenReturn(Optional.of(cat))

        val saved = SubcategoriaPersistenceEntity(
            idSubcategoria = 7L,
            nome = "Uber",
            categoria = cat
        )
        Mockito.`when`(repo.save(Mockito.any(SubcategoriaPersistenceEntity::class.java))).thenReturn(saved)

        val result = adapter.create("Uber", 10L)

        assertEquals(7L, result.idSubcategoria)
        assertEquals("Uber", result.nome)
        assertEquals(10L, result.idCategoria)

        Mockito.verify(categoriaRepo).findById(10L)
        Mockito.verify(repo).save(Mockito.any(SubcategoriaPersistenceEntity::class.java))
    }

    @Test
    fun `create deve lançar NotFound quando categoria não existe`() {
        Mockito.`when`(categoriaRepo.findById(999L)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            adapter.create("Uber", 999L)
        }
        assertEquals("Categoria não encontrada", ex.message)

        Mockito.verify(repo, Mockito.never()).save(Mockito.any(SubcategoriaPersistenceEntity::class.java))
    }

    @Test
    fun `update deve atualizar nome e categoria, salvar e mapear`() {
        val catOld = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val catNew = CategoriaPersistenceEntity(idCategoria = 2L, nome = "Alimentação")

        val existing = SubcategoriaPersistenceEntity(
            idSubcategoria = 5L,
            nome = "Uber",
            categoria = catOld
        )

        Mockito.`when`(repo.findById(5L)).thenReturn(Optional.of(existing))
        Mockito.`when`(categoriaRepo.findById(2L)).thenReturn(Optional.of(catNew))
        Mockito.`when`(repo.save(Mockito.any(SubcategoriaPersistenceEntity::class.java))).thenAnswer { it.arguments[0] }

        val result = adapter.update(5L, "Restaurante", 2L)

        assertEquals(5L, result.idSubcategoria)
        assertEquals("Restaurante", result.nome)
        assertEquals(2L, result.idCategoria)

        // garante mutação do entity
        assertEquals("Restaurante", existing.nome)
        assertEquals(catNew, existing.categoria)

        Mockito.verify(repo).findById(5L)
        Mockito.verify(categoriaRepo).findById(2L)
        Mockito.verify(repo).save(Mockito.any(SubcategoriaPersistenceEntity::class.java))
    }

    @Test
    fun `update deve lançar NotFound quando subcategoria não existe`() {
        Mockito.`when`(repo.findById(999L)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            adapter.update(999L, "X", 1L)
        }
        assertEquals("Subcategoria não encontrada", ex.message)

        Mockito.verify(categoriaRepo, Mockito.never()).findById(Mockito.anyLong())
        Mockito.verify(repo, Mockito.never()).save(Mockito.any(SubcategoriaPersistenceEntity::class.java))
    }

    @Test
    fun `update deve lançar NotFound quando categoria não existe`() {
        val catOld = CategoriaPersistenceEntity(idCategoria = 1L, nome = "Transporte")
        val existing = SubcategoriaPersistenceEntity(
            idSubcategoria = 5L,
            nome = "Uber",
            categoria = catOld
        )

        Mockito.`when`(repo.findById(5L)).thenReturn(Optional.of(existing))
        Mockito.`when`(categoriaRepo.findById(999L)).thenReturn(Optional.empty())

        val ex = assertThrows(NotFoundException::class.java) {
            adapter.update(5L, "X", 999L)
        }
        assertEquals("Categoria não encontrada", ex.message)

        Mockito.verify(repo, Mockito.never()).save(Mockito.any(SubcategoriaPersistenceEntity::class.java))
    }

    @Test
    fun `delete deve delegar para repo`() {
        adapter.delete(10L)
        Mockito.verify(repo).deleteById(10L)
    }

    @Test
    fun `findById deve mapear quando existe`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 10L, nome = "Transporte")
        val entity = SubcategoriaPersistenceEntity(
            idSubcategoria = 7L,
            nome = "Uber",
            categoria = cat
        )
        Mockito.`when`(repo.findById(7L)).thenReturn(Optional.of(entity))

        val result = adapter.findById(7L)

        assertNotNull(result)
        assertEquals(7L, result!!.idSubcategoria)
        assertEquals("Uber", result.nome)
        assertEquals(10L, result.idCategoria)
    }

    @Test
    fun `findById deve retornar null quando não existe`() {
        Mockito.`when`(repo.findById(7L)).thenReturn(Optional.empty())
        val result = adapter.findById(7L)
        assertNull(result)
    }

    @Test
    fun `search deve retornar findAll quando nome é null ou blank`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 10L, nome = "Transporte")
        val e1 = SubcategoriaPersistenceEntity(idSubcategoria = 1L, nome = "Uber", categoria = cat)
        val e2 = SubcategoriaPersistenceEntity(idSubcategoria = 2L, nome = "Ônibus", categoria = cat)

        Mockito.`when`(repo.findAll()).thenReturn(listOf(e1, e2))

        val result = adapter.search(null)

        assertEquals(2, result.size)
        Mockito.verify(repo).findAll()
        Mockito.verify(repo, Mockito.never()).findAllByNomeContainingIgnoreCase(Mockito.anyString())
    }

    @Test
    fun `search deve filtrar por nome quando informado`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 10L, nome = "Transporte")
        val e1 = SubcategoriaPersistenceEntity(idSubcategoria = 1L, nome = "Uber", categoria = cat)

        Mockito.`when`(repo.findAllByNomeContainingIgnoreCase("ub")).thenReturn(listOf(e1))

        val result = adapter.search("ub")

        assertEquals(1, result.size)
        assertEquals("Uber", result[0].nome)
        Mockito.verify(repo).findAllByNomeContainingIgnoreCase("ub")
        Mockito.verify(repo, Mockito.never()).findAll()
    }

    @Test
    fun `existsByNomeInCategoria deve delegar`() {
        Mockito.`when`(repo.existsByNomeAndCategoria_IdCategoria("Uber", 10L)).thenReturn(true)

        assertTrue(adapter.existsByNomeInCategoria("Uber", 10L))
        Mockito.verify(repo).existsByNomeAndCategoria_IdCategoria("Uber", 10L)
    }

    @Test
    fun `findAllByCategoria deve mapear lista`() {
        val cat = CategoriaPersistenceEntity(idCategoria = 10L, nome = "Transporte")
        val e1 = SubcategoriaPersistenceEntity(idSubcategoria = 1L, nome = "Uber", categoria = cat)
        val e2 = SubcategoriaPersistenceEntity(idSubcategoria = 2L, nome = "Ônibus", categoria = cat)

        Mockito.`when`(repo.findAllByCategoria_IdCategoria(10L)).thenReturn(listOf(e1, e2))

        val result = adapter.findAllByCategoria(10L)

        assertEquals(2, result.size)
        assertEquals(10L, result[0].idCategoria)
        assertEquals("Uber", result[0].nome)
        Mockito.verify(repo).findAllByCategoria_IdCategoria(10L)
    }
}
