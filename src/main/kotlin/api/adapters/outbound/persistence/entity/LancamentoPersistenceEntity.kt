package com.controlefinanceiro.api.adapters.outbound.persistence.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDate

@Entity
@Table(name = "lancamentos")
class LancamentoPersistenceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lancamento")
    var idLancamento: Long? = null,

    @Column(name = "valor", nullable = false, precision = 19, scale = 2)
    var valor: BigDecimal = BigDecimal.ZERO,

    @Column(name = "data", nullable = false)
    var data: LocalDate = LocalDate.now(),

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_subcategoria", nullable = false)
    var subcategoria: SubcategoriaPersistenceEntity? = null,

    @Column(name = "comentario")
    var comentario: String? = null
)
