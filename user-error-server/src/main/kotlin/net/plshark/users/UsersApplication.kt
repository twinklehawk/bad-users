package net.plshark.users

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@SpringBootApplication
@EnableR2dbcRepositories("net.plshark.users.repo.springdata")
class UsersApplication

fun main(args: Array<String>) {
    @Suppress("SpreadOperator")
    runApplication<UsersApplication>(*args)
}
