package com.controlefinanceiro.api.application.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DateUtilTest {

    @Test
    fun `parseOrNull deve parsear dd-MM-yyyy`() {
        val date = DateUtil.parseOrNull("20/12/2025")
        assertEquals(LocalDate.of(2025, 12, 20), date)
    }

    @Test
    fun `parseOrNull deve retornar null para invalida`() {
        assertNull(DateUtil.parseOrNull("2025-12-20"))
        assertNull(DateUtil.parseOrNull("32/12/2025"))
        assertNull(DateUtil.parseOrNull(null))
    }

    @Test
    fun `format deve formatar dd-MM-yyyy`() {
        assertEquals("01/12/2025", DateUtil.format(LocalDate.of(2025, 12, 1)))
    }
}
