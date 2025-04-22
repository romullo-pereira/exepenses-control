package com.romullo.pereira.expensensecontrol

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<ExpensenseControlApplication>().with(TestcontainersConfiguration::class).run(*args)
}
