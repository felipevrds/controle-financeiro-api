package com.controlefinanceiro.api.adapters.inbound.rest

import com.controlefinanceiro.api.application.dto.LancamentoCreateRequestDto
import com.controlefinanceiro.api.application.dto.LancamentoResponseDto
import com.controlefinanceiro.api.application.dto.LancamentoUpdateRequestDto
import com.controlefinanceiro.api.application.usecase.CreateLancamentoUseCase
import com.controlefinanceiro.api.application.usecase.DeleteLancamentoUseCase
import com.controlefinanceiro.api.application.usecase.GetLancamentoUseCase
import com.controlefinanceiro.api.application.usecase.SearchLancamentoUseCase
import com.controlefinanceiro.api.application.usecase.UpdateLancamentoUseCase
import com.controlefinanceiro.api.application.util.DateUtil
import com.controlefinanceiro.api.exception.ValidationException
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/lancamentos")
class LancamentoController(
    private val createUseCase: CreateLancamentoUseCase,
    private val updateUseCase: UpdateLancamentoUseCase,
    private val deleteUseCase: DeleteLancamentoUseCase,
    private val getUseCase: GetLancamentoUseCase,
    private val searchUseCase: SearchLancamentoUseCase
) {
    @GetMapping
    fun search(@RequestParam(required = false) data: String?): List<LancamentoResponseDto> {
        val dataParsed = DateUtil.parseOrNull(data)
        if (data != null && dataParsed == null) throw ValidationException("Data inválida")

        return searchUseCase.execute(dataParsed).map {
            LancamentoResponseDto(
                id_lancamento = it.idLancamento,
                valor = it.valor.setScale(2).toPlainString(),
                data = DateUtil.format(it.data),
                id_subcategoria = it.idSubcategoria,
                comentario = it.comentario
            )
        }
    }

    @GetMapping("/{id_lancamento}")
    fun get(@PathVariable("id_lancamento") id: Long): LancamentoResponseDto {
        val lancamento = getUseCase.execute(id)
        return LancamentoResponseDto(
            id_lancamento = lancamento.idLancamento,
            valor = lancamento.valor.setScale(2).toPlainString(),
            data = DateUtil.format(lancamento.data),
            id_subcategoria = lancamento.idSubcategoria,
            comentario = lancamento.comentario
        )
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid body: LancamentoCreateRequestDto): LancamentoResponseDto {
        val dataParsed = DateUtil.parseOrNull(body.data)
        if (body.data != null && dataParsed == null) throw ValidationException("Data inválida")

        val lancamento = createUseCase.execute(body.valor, dataParsed, body.id_subcategoria, body.comentario)
        return LancamentoResponseDto(
            id_lancamento = lancamento.idLancamento,
            valor = lancamento.valor.setScale(2).toPlainString(),
            data = DateUtil.format(lancamento.data),
            id_subcategoria = lancamento.idSubcategoria,
            comentario = lancamento.comentario
        )
    }

    @PutMapping("/{id_lancamento}")
    fun update(
        @PathVariable("id_lancamento") id: Long,
        @RequestBody @Valid body: LancamentoUpdateRequestDto
    ): LancamentoResponseDto {
        val dataParsed = DateUtil.parseOrNull(body.data)
        if (body.data != null && dataParsed == null) throw ValidationException("Data inválida")

        val lancamento = updateUseCase.execute(id, body.valor, dataParsed, body.id_subcategoria, body.comentario)
        return LancamentoResponseDto(
            id_lancamento = lancamento.idLancamento,
            valor = lancamento.valor.setScale(2).toPlainString(),
            data = DateUtil.format(lancamento.data),
            id_subcategoria = lancamento.idSubcategoria,
            comentario = lancamento.comentario
        )
    }

    @DeleteMapping("/{id_lancamento}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable("id_lancamento") id: Long) {
        deleteUseCase.execute(id)
    }
}
