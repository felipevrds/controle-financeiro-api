package com.controlefinanceiro.api.support.inmemory

import com.controlefinanceiro.api.domain.entity.SubcategoriaEntity
import com.controlefinanceiro.api.domain.port.SubcategoriaRepositoryPort
import java.util.concurrent.atomic.AtomicLong

class InMemorySubcategoriaRepository : SubcategoriaRepositoryPort {
    private val seq = AtomicLong(1)
    private val data = linkedMapOf<Long, SubcategoriaEntity>()

    override fun create(nome: String, idCategoria: Long): SubcategoriaEntity {
        val id = seq.getAndIncrement()
        val entity = SubcategoriaEntity(idSubcategoria = id, nome = nome, idCategoria = idCategoria)
        data[id] = entity
        return entity
    }

    override fun update(idSubcategoria: Long, nome: String, idCategoria: Long): SubcategoriaEntity {
        val current = data[idSubcategoria] ?: return create(nome, idCategoria).copy(idSubcategoria = idSubcategoria)
        val updated = current.copy(nome = nome, idCategoria = idCategoria)
        data[idSubcategoria] = updated
        return updated
    }

    override fun delete(idSubcategoria: Long) {
        data.remove(idSubcategoria)
    }

    override fun findById(idSubcategoria: Long): SubcategoriaEntity? = data[idSubcategoria]

    override fun search(nome: String?): List<SubcategoriaEntity> {
        if (nome.isNullOrBlank()) return data.values.toList()
        val q = nome.lowercase()
        return data.values.filter { it.nome.lowercase().contains(q) }
    }

    override fun existsByNomeInCategoria(nome: String, idCategoria: Long): Boolean {
        val q = nome.lowercase()
        return data.values.any { it.idCategoria == idCategoria && it.nome.lowercase() == q }
    }

    override fun findAllByCategoria(idCategoria: Long): List<SubcategoriaEntity> =
        data.values.filter { it.idCategoria == idCategoria }

    fun seed(entity: SubcategoriaEntity) {
        data[entity.idSubcategoria] = entity
    }
}
