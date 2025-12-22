package com.controlefinanceiro.api.application.usecase

import com.controlefinanceiro.api.domain.entity.LancamentoEntity
import com.controlefinanceiro.api.domain.port.LancamentoRepositoryPort
import com.controlefinanceiro.api.domain.port.SubcategoriaRepositoryPort
import com.controlefinanceiro.api.exception.NotFoundException
import java.time.LocalDate

class CreateLancamentoUseCase(
    private val lancRepo: LancamentoRepositoryPort,
    private val subRepo: SubcategoriaRepositoryPort
) {
    fun execute(valor: String, data: LocalDate?, idSubcategoria: Long, comentario: String?): LancamentoEntity {
        subRepo.findById(idSubcategoria) ?: throw NotFoundException("Subcategoria não encontrada")
        return lancRepo.create(valor, data, idSubcategoria, comentario)
    }
}

class UpdateLancamentoUseCase(
    private val lancRepo: LancamentoRepositoryPort,
    private val subRepo: SubcategoriaRepositoryPort
) {
    fun execute(id: Long, valor: String, data: LocalDate?, idSubcategoria: Long, comentario: String?): LancamentoEntity {
        lancRepo.findById(id) ?: throw NotFoundException("Lançamento não encontrado")
        subRepo.findById(idSubcategoria) ?: throw NotFoundException("Subcategoria não encontrada")
        return lancRepo.update(id, valor, data, idSubcategoria, comentario)
    }
}

class DeleteLancamentoUseCase(private val repo: LancamentoRepositoryPort) {
    fun execute(id: Long) {
        repo.findById(id) ?: throw NotFoundException("Lançamento não encontrado")
        repo.delete(id)
    }
}

class GetLancamentoUseCase(private val repo: LancamentoRepositoryPort) {
    fun execute(id: Long): LancamentoEntity =
        repo.findById(id) ?: throw NotFoundException("Lançamento não encontrado")
}

class SearchLancamentoUseCase(private val repo: LancamentoRepositoryPort) {
    fun execute(data: LocalDate?): List<LancamentoEntity> = repo.search(data)
}
