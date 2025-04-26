package com.romullo.pereira.expensensecontrol

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan
class ExpensenseControlApplication

fun main(args: Array<String>) {
    runApplication<ExpensenseControlApplication>(*args)
}
