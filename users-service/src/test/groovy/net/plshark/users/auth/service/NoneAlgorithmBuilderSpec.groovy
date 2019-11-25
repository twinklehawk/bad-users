package net.plshark.users.auth.service

import net.plshark.users.auth.AuthProperties
import spock.lang.Specification

class NoneAlgorithmBuilderSpec extends Specification{

    def builder = new NoneAlgorithmBuilder()

    def 'none should load the none algorithm'() {
        def props = AuthProperties.forNone(NoneAlgorithmBuilder.NONE, 'bad-users', 1000)

        when:
        def algorithm = builder.build(props)

        then:
        algorithm.name == 'none'
    }

    def 'should return null if the name is not none'() {
        def props = AuthProperties.forNone('bad', 'bad-users', 1000)

        expect:
        builder.build(props) == null
    }
}