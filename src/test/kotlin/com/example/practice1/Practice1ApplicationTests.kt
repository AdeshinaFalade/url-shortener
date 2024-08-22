package com.example.practice1

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import

@Import(TestcontainersConfiguration::class)
@SpringBootTest
class Practice1ApplicationTests {

	@Test
	fun contextLoads() {
	}

}
