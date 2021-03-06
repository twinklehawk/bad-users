package net.plshark.users.auth.webservice

import net.plshark.users.auth.model.AccountCredentials
import net.plshark.users.auth.model.AuthToken
import net.plshark.users.auth.model.AuthenticatedUser
import net.plshark.users.auth.service.AuthService
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun authenticate(@RequestBody credentials: AccountCredentials): AuthToken {
        return authService.authenticate(credentials)
    }

    @PostMapping(
        value = ["/refresh"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun refresh(@RequestBody refreshToken: String): AuthToken {
        return authService.refresh(refreshToken)
    }

    @PostMapping(
        value = ["/validate"],
        consumes = [MediaType.TEXT_PLAIN_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    suspend fun validateToken(@RequestBody accessToken: String): AuthenticatedUser {
        return authService.validateToken(accessToken)
    }
}
