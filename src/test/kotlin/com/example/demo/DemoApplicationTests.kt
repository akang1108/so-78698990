package com.example.demo

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@SpringBootTest
@AutoConfigureWebTestClient
class DemoApplicationTests {

	@Autowired
	private lateinit var client: WebTestClient

	@Test
	fun noFinal() {
		client.get().uri("/users/1")
			.header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
			.exchange()
			.expectStatus().isForbidden()
	}

	@Test
	fun final() {
		client.get().uri("/final/users/1")
			.header(HttpHeaders.AUTHORIZATION, basicAuthHeader)
			.exchange()
			.expectStatus().isForbidden()
	}

	companion object {
		val basicAuthHeader = "basic " + Base64.getEncoder().encodeToString("user:user".toByteArray(Charsets.UTF_8))
	}
}
