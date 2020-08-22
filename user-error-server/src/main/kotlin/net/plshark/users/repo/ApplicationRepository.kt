package net.plshark.users.repo

import net.plshark.users.model.Application
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

/**
 * Repository for saving, deleting, and retrieving applications
 */
interface ApplicationRepository : ReactiveCrudRepository<Application, Long> {

    /**
     * Find an application by name
     * @param name the applications name
     * @return a [Mono] emitting the matching application or empty if not found
     */
    fun findByName(name: String): Mono<Application>

    /**
     * Delete an application by name
     * @param name the application name
     * @return a [Mono] signalling when complete
     */
    fun deleteByName(name: String): Mono<Void>
}
