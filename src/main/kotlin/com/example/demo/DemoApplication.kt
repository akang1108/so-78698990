package com.example.demo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
	runApplication<DemoApplication>(*args)
}

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity(useAuthorizationManager = true)
class WebAuthorizationConfig {
	@Bean
	fun userDetailsService(): ReactiveUserDetailsService {
		val userDetails = User.withDefaultPasswordEncoder()
			.username("user")
			.password("user")
			.roles("USER")
			.build()
		return MapReactiveUserDetailsService(userDetails)
	}

	@Bean
	fun springSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
		return http {
			authorizeExchange {
				authorize(anyExchange, authenticated)
			}
			formLogin { }
			httpBasic { }
		}
	}
}

@RestController
class UserController(private val userService: UserService) {
	@GetMapping("/users/{uid}")
	final suspend fun getUser(@PathVariable("uid") uid: String): ResponseEntity<String> {
		return withContext(Dispatchers.IO) {
			ResponseEntity.ok(userService.getUser(uid))
		}
	}

	@GetMapping("/final/users/{uid}")
	final suspend fun getFinalUser(@PathVariable("uid") uid: String): ResponseEntity<String> {
		return withContext(Dispatchers.IO) {
			ResponseEntity.ok(userService.getFinalUser(uid))
		}
	}
}

@Service
class DefaultUserService: UserService {
	@PreAuthorize("denyAll()")
	override suspend fun getUser(uid: String): String {
		return "user"
	}

	@PreAuthorize("denyAll()")
	final override suspend fun getFinalUser(uid: String): String {
		return "user"
	}
}

interface UserService {
	suspend fun getUser(uid: String): String
	suspend fun getFinalUser(uid: String): String
}
