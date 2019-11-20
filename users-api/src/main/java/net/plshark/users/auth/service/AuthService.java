package net.plshark.users.auth.service;

import net.plshark.users.auth.model.AccountCredentials;
import net.plshark.users.auth.model.AuthToken;
import net.plshark.users.auth.model.AuthenticatedUser;
import reactor.core.publisher.Mono;

/**
 * Service to authenticate a user using tokens
 */
public interface AuthService {

    /**
     * Authenticate a user and generate an auth token
     * @param credentials the user credentials
     * @return the auth token if successful or a BadCredentialsException if the credentials are invalid
     */
    Mono<AuthToken> authenticate(AccountCredentials credentials);

    /**
     * Refresh an existing token
     * @param refreshToken the existing token
     * @return a new auth token or a BadCredentialsException if the refresh token is invalid
     */
    Mono<AuthToken> refresh(String refreshToken);

    /**
     * Validate an access token
     * @param accessToken the access token
     * @return the username and authorities if the token is valid or a BadCredentialsException if not
     */
    Mono<AuthenticatedUser> validateToken(String accessToken);
}
