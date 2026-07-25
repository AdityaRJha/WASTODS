package com.solEvictus.curator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CuratorApplication

fun main(args: Array<String>) {
	runApplication<CuratorApplication>(*args)
}
