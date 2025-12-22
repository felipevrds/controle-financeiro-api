package com.controlefinanceiro.api.adapters.outbound.persistence

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.CategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.CategoriaJpaRepository
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.SubcategoriaJpaRepository
import com.controlefinanceiro.api.domain.entity.CategoriaEntity
import com.controlefinanceiro.api.domain.port.CategoriaRepositoryPort
import com.controlefinanceiro.api.exception.NotFoundException
import org.springframework.stereotype.Component

@Component
class CategoriaRepositoryAdapter(
    private val repo: CategoriaJpaRepository,
    private val subRepo: SubcategoriaJpaRepository
) : CategoriaRepositoryPort {

    override fun create(nome: String): CategoriaEntity {
        val saved = repo.save(CategoriaPersistenceEntity(nome = nome))
        return CategoriaEntity(saved.idCategoria!!, saved.nome)
    }

    override fun update(idCategoria: Long, nome: String): CategoriaEntity {
        val entity = repo.findById(idCategoria).orElseThrow { NotFoundException("Categoria não encontrada") }
        entity.nome = nome
        val saved = repo.save(entity)
        return CategoriaEntity(saved.idCategoria!!, saved.nome)
    }

    override fun delete(idCategoria: Long) {
        val subs = subRepo.findAllByCategoria_IdCategoria(idCategoria)
        subRepo.deleteAll(subs)
        repo.deleteById(idCategoria)
    }

    override fun findById(idCategoria: Long): CategoriaEntity? =
        repo.findById(idCategoria).map { CategoriaEntity(it.idCategoria!!, it.nome) }.orElse(null)

    override fun search(nome: String?): List<CategoriaEntity> {
        val list = if (nome.isNullOrBlank()) repo.findAll() else repo.findAllByNomeContainingIgnoreCase(nome)
        return list.map { CategoriaEntity(it.idCategoria!!, it.nome) }
    }

    override fun existsByNome(nome: String): Boolean = repo.existsByNome(nome)
}
