package com.controlefinanceiro.api.adapters.inbound.rest

import com.controlefinanceiro.api.application.dto.BalancoResponseDto
import com.controlefinanceiro.api.application.dto.CategoriaResponseDto
import com.controlefinanceiro.api.application.util.DateUtil
import com.controlefinanceiro.api.domain.service.BalancoService
import com.controlefinanceiro.api.exception.ValidationException
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/balanco")
class BalancoController(
    private val balancoService: BalancoService
) {
    @GetMapping
    fun get(
        @RequestParam("data_inicio") dataInicio: String,
        @RequestParam("data_fim") dataFim: String,
        @RequestParam("id_categoria", required = false) idCategoria: Long?
    ): BalancoResponseDto {
        val dataInicio = DateUtil.parseOrNull(dataInicio) ?: throw ValidationException("Data inválida")
        val dataFim = DateUtil.parseOrNull(dataFim) ?: throw ValidationException("Data inválida")

        val result = balancoService.calcular(dataInicio, dataFim, idCategoria)

        val categoriaDto = if (result.idCategoria != null) {
            // Aqui pode ser necessário colocar para retornar idCategoria !!, se quebrar quando não tiver esse filtro
            CategoriaResponseDto(result.idCategoria, result.nomeCategoria!!)
        } else null

        return BalancoResponseDto(
            categoria = categoriaDto,
            receita = result.receita.setScale(2).toPlainString(),
            despesa = result.despesa.setScale(2).toPlainString(),
            saldo = result.saldo.setScale(2).toPlainString()
        )
    }
}
