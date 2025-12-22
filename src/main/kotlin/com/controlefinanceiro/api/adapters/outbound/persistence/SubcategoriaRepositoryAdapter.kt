package com.controlefinanceiro.api.adapters.outbound.persistence

import com.controlefinanceiro.api.adapters.outbound.persistence.entity.SubcategoriaPersistenceEntity
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.CategoriaJpaRepository
import com.controlefinanceiro.api.adapters.outbound.persistence.repository.SubcategoriaJpaRepository
import com.controlefinanceiro.api.domain.entity.SubcategoriaEntity
import com.controlefinanceiro.api.domain.port.SubcategoriaRepositoryPort
import com.controlefinanceiro.api.exception.NotFoundException
import org.springframework.stereotype.Component

@Component
class SubcategoriaRepositoryAdapter(
    private val repo: SubcategoriaJpaRepository,
    private val categoriaRepo: CategoriaJpaRepository
) : SubcategoriaRepositoryPort {

    override fun create(nome: String, idCategoria: Long): SubcategoriaEntity {
        val cat = categoriaRepo.findById(idCategoria).orElseThrow { NotFoundException("Categoria não encontrada") }
        val saved = repo.save(SubcategoriaPersistenceEntity(nome = nome, categoria = cat))
        return SubcategoriaEntity(saved.idSubcategoria!!, saved.nome, saved.categoria!!.idCategoria!!)
    }

    override fun update(idSubcategoria: Long, nome: String, idCategoria: Long): SubcategoriaEntity {
        val entity = repo.findById(idSubcategoria).orElseThrow { NotFoundException("Subcategoria não encontrada") }
        val cat = categoriaRepo.findById(idCategoria).orElseThrow { NotFoundException("Categoria não encontrada") }
        entity.nome = nome
        entity.categoria = cat
        val saved = repo.save(entity)
        return SubcategoriaEntity(saved.idSubcategoria!!, saved.nome, saved.categoria!!.idCategoria!!)
    }

    override fun delete(idSubcategoria: Long) {
        repo.deleteById(idSubcategoria)
    }

    override fun findById(idSubcategoria: Long): SubcategoriaEntity? =
        repo.findById(idSubcategoria).map {
            SubcategoriaEntity(it.idSubcategoria!!, it.nome, it.categoria!!.idCategoria!!)
        }.orElse(null)

    override fun search(nome: String?): List<SubcategoriaEntity> {
        val list = if (nome.isNullOrBlank()) repo.findAll() else repo.findAllByNomeContainingIgnoreCase(nome)
        return list.map { SubcategoriaEntity(it.idSubcategoria!!, it.nome, it.categoria!!.idCategoria!!) }
    }

    override fun existsByNomeInCategoria(nome: String, idCategoria: Long): Boolean =
        repo.existsByNomeAndCategoria_IdCategoria(nome, idCategoria)

    override fun findAllByCategoria(idCategoria: Long): List<SubcategoriaEntity> =
        repo.findAllByCategoria_IdCategoria(idCategoria).map {
            SubcategoriaEntity(it.idSubcategoria!!, it.nome, it.categoria!!.idCategoria!!)
        }
}
