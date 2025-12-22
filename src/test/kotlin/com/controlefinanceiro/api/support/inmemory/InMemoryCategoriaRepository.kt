package com.controlefinanceiro.api.support.inmemory

import com.controlefinanceiro.api.domain.entity.CategoriaEntity
import com.controlefinanceiro.api.domain.port.CategoriaRepositoryPort
import java.util.concurrent.atomic.AtomicLong

class InMemoryCategoriaRepository : CategoriaRepositoryPort {
    private val seq = AtomicLong(1)
    private val data = linkedMapOf<Long, CategoriaEntity>()

    override fun create(nome: String): CategoriaEntity {
        val id = seq.getAndIncrement()
        val entity = CategoriaEntity(idCategoria = id, nome = nome)
        data[id] = entity
        return entity
    }

    override fun update(idCategoria: Long, nome: String): CategoriaEntity {
        val current = data[idCategoria] ?: return create(nome).copy(idCategoria = idCategoria) // defensive, should not happen if use case checks
        val updated = current.copy(nome = nome)
        data[idCategoria] = updated
        return updated
    }

    override fun delete(idCategoria: Long) {
        data.remove(idCategoria)
    }

    override fun findById(idCategoria: Long): CategoriaEntity? = data[idCategoria]

    override fun search(nome: String?): List<CategoriaEntity> {
        if (nome.isNullOrBlank()) return data.values.toList()
        val q = nome.lowercase()
        return data.values.filter { it.nome.lowercase().contains(q) }
    }

    override fun existsByNome(nome: String): Boolean {
        val q = nome.lowercase()
        return data.values.any { it.nome.lowercase() == q }
    }
}
