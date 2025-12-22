package com.controlefinanceiro.api.config

import com.controlefinanceiro.api.application.usecase.*
import com.controlefinanceiro.api.domain.port.CategoriaRepositoryPort
import com.controlefinanceiro.api.domain.port.SubcategoriaRepositoryPort
import com.controlefinanceiro.api.domain.port.LancamentoRepositoryPort
import com.controlefinanceiro.api.domain.service.BalancoService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class UseCaseConfig {

    @Bean fun createCategoriaUseCase(repo: CategoriaRepositoryPort) = CreateCategoriaUseCase(repo)
    @Bean fun updateCategoriaUseCase(repo: CategoriaRepositoryPort) = UpdateCategoriaUseCase(repo)
    @Bean fun deleteCategoriaUseCase(catRepo: CategoriaRepositoryPort, lancRepo: LancamentoRepositoryPort) =
        DeleteCategoriaUseCase(catRepo, lancRepo)
    @Bean fun getCategoriaUseCase(repo: CategoriaRepositoryPort) = GetCategoriaUseCase(repo)
    @Bean fun searchCategoriaUseCase(repo: CategoriaRepositoryPort) = SearchCategoriaUseCase(repo)

    @Bean fun createSubcategoriaUseCase(subRepo: SubcategoriaRepositoryPort, catRepo: CategoriaRepositoryPort) =
        CreateSubcategoriaUseCase(subRepo, catRepo)
    @Bean fun updateSubcategoriaUseCase(subRepo: SubcategoriaRepositoryPort, catRepo: CategoriaRepositoryPort) =
        UpdateSubcategoriaUseCase(subRepo, catRepo)
    @Bean fun deleteSubcategoriaUseCase(subRepo: SubcategoriaRepositoryPort, lancRepo: LancamentoRepositoryPort) =
        DeleteSubcategoriaUseCase(subRepo, lancRepo)
    @Bean fun getSubcategoriaUseCase(repo: SubcategoriaRepositoryPort) = GetSubcategoriaUseCase(repo)
    @Bean fun searchSubcategoriaUseCase(repo: SubcategoriaRepositoryPort) = SearchSubcategoriaUseCase(repo)

    @Bean fun createLancamentoUseCase(lancRepo: LancamentoRepositoryPort, subRepo: SubcategoriaRepositoryPort) =
        CreateLancamentoUseCase(lancRepo, subRepo)
    @Bean fun updateLancamentoUseCase(lancRepo: LancamentoRepositoryPort, subRepo: SubcategoriaRepositoryPort) =
        UpdateLancamentoUseCase(lancRepo, subRepo)
    @Bean fun deleteLancamentoUseCase(repo: LancamentoRepositoryPort) = DeleteLancamentoUseCase(repo)
    @Bean fun getLancamentoUseCase(repo: LancamentoRepositoryPort) = GetLancamentoUseCase(repo)
    @Bean fun searchLancamentoUseCase(repo: LancamentoRepositoryPort) = SearchLancamentoUseCase(repo)

    @Bean fun balancoService(catRepo: CategoriaRepositoryPort, lancRepo: LancamentoRepositoryPort) =
        BalancoService(catRepo, lancRepo)
}
