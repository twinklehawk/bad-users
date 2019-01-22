package net.plshark.auth.jwt;

import net.plshark.auth.model.AuthenticatedUser;
import net.plshark.auth.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Authentication manager that validates JWT authentication
 */
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final AuthService authService;

    /**
     * Create a new instance
     * @param authService the service to use to authenticate
     */
    public JwtReactiveAuthenticationManager(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        return Mono.just(authentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .map(auth -> (JwtAuthenticationToken) auth)
                .map(JwtAuthenticationToken::getCredentials)
                .publishOn(Schedulers.parallel())
                .flatMap(this::verifyToken)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new BadCredentialsException("Invalid credentials"))))
                .map(user -> JwtAuthenticationToken.builder()
                        .withUsername(user.getUsername())
                        .withAuthorities(user.getAuthorities())
                        .withAuthenticated(true)
                        .build());
    }

    /**
     * Verify and decode a JWT
     * @param token the JWT
     * @return user info from the JWT or BadCredentialsException if the token is invalid
     */
    private Mono<AuthenticatedUser> verifyToken(String token) {
        return authService.validateToken(token);
    }
}
