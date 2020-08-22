package net.plshark.users.repo.springdata

import io.r2dbc.spi.Row
import net.plshark.users.model.Application
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.Optional

/**
 * Role repository that uses spring data and r2dbc
 */
@Repository
class SpringDataApplicationsRepository(private val client: DatabaseClient) {

    fun get(id: Long): Mono<Application> {
        return client.execute("SELECT * FROM application WHERE id = :id")
            .bind("id", id)
            .map { row -> mapRow(row) }
            .one()
    }

    fun get(name: String): Mono<Application> {
        return client.execute("SELECT * FROM application WHERE name = :name")
            .bind("name", name)
            .map { row -> mapRow(row) }
            .one()
    }

    fun insert(application: Application): Mono<Application> {
        return client.execute("INSERT INTO application (name) VALUES (:name) RETURNING id")
            .bind("name", application.name)
            .fetch().one()
            .flatMap { map ->
                Optional.ofNullable(map["id"] as Long?)
                    .map { data -> Mono.just(data) }
                    .orElse(Mono.empty())
            }
            .switchIfEmpty(Mono.error { IllegalStateException("No ID returned from insert") })
            .map { id -> Application(id = id, name = application.name) }
    }

    fun delete(id: Long): Mono<Void> {
        return client.execute("DELETE FROM application WHERE id = :id")
            .bind("id", id)
            .then()
    }

    fun delete(name: String): Mono<Void> {
        return client.execute("DELETE FROM application WHERE name = :name")
            .bind("name", name)
            .then()
    }

    companion object {
        fun mapRow(row: Row): Application {
            return Application(
                id = row["id", java.lang.Long::class.java]!!.toLong(),
                name = row["name", String::class.java]!!
            )
        }
    }
}
