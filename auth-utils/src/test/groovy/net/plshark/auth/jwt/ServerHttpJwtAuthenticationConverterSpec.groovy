package net.plshark.auth.jwt

import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import reactor.test.StepVerifier
import spock.lang.Specification

class ServerHttpJwtAuthenticationConverterSpec extends Specification {

    def converter = new ServerHttpJwtAuthenticationConverter()

    def 'should parse out the token from the authorization header'() {
        //noinspection GroovyAssignabilityCheck
        def exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("http://test/url").header('Authorization', 'Bearer test-token'))

        expect:
        StepVerifier.create(converter.apply(exchange))
                .expectNext(JwtAuthenticationToken.builder().withToken('test-token').build())
                .verifyComplete()
    }

    def 'should return empty when there is no authorization header'() {
        def exchange = MockServerWebExchange.from(MockServerHttpRequest.get("http://test/url"))

        expect:
        StepVerifier.create(converter.apply(exchange))
                .verifyComplete()
    }

    def 'should return empty when the header value does not start with Bearer'() {
        //noinspection GroovyAssignabilityCheck
        def exchange = MockServerWebExchange.from(
                MockServerHttpRequest.get("http://test/url").header('Authorization', 'test-token'))

        expect:
        StepVerifier.create(converter.apply(exchange))
                .verifyComplete()
    }
}