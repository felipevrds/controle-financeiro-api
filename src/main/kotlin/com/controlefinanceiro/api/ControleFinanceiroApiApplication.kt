package com.controlefinanceiro.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(proxyBeanMethods = false)
class ControleFinanceiroApiApplication

fun main(args: Array<String>) {
    runApplication<com.controlefinanceiro.api.ControleFinanceiroApiApplication>(*args)
}
