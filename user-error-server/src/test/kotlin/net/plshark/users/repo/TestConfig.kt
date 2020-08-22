package net.plshark.users.repo

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import net.plshark.testutils.IntTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories
class TestConfig : AbstractR2dbcConfiguration() {

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return ConnectionFactories.get(IntTest.DB_URL)
    }
}
