package com.example.practice1

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<Practice1Application>().with(TestcontainersConfiguration::class).run(*args)
}
