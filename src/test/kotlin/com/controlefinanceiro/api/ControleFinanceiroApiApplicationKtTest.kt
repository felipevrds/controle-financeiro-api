package com.controlefinanceiro.api

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.lang.reflect.Modifier
import kotlin.jvm.java

class ControleFinanceiroApiApplicationKtTest {

    @Test
    fun `application class deve estar anotada e nao ser final`() {
        val appClass = ControleFinanceiroApiApplication::class.java

        // A anotação @SpringBootApplication é importante porque configura component-scan e auto-config.
        assertNotNull(appClass.getAnnotation(SpringBootApplication::class.java))

        // Em Kotlin, classes são 'final' por padrão. O plugin kotlin-spring (allOpen) deve "abrir" essa classe.
        assertFalse(Modifier.isFinal(appClass.modifiers), "A classe ControleFinanceiroApiApplication nao deve ser final (verifique o plugin kotlin-spring/allOpen).")
    }

    @Test
    fun `deve existir metodo main na classe gerada Kt sem executar o Spring`() {
        val ktClass = Class.forName("com.controlefinanceiro.api.ControleFinanceiroApiApplicationKt")
        val main = ktClass.getDeclaredMethod("main", Array<String>::class.java)
        assertTrue(Modifier.isStatic(main.modifiers), "O metodo main deve ser static (top-level Kotlin).")
    }
}
