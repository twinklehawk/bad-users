package net.plshark.users.service

import net.plshark.users.model.Application
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing applications
 */
interface ApplicationsService {

    /**
     * Retrieve an application by name
     * @param name the application name
     * @return a [Mono] emitting the matching application or empty if not found
     */
    fun findByName(name: String): Mono<Application>

    /**
     * Retrieve all applications
     * @return a [Flux] emitting the applications
     */
    fun findAll(): Flux<Application>

    // TODO pagination

    /**
     * Save a new application
     * @param application the application, the ID must be 0
     * @return a [Mono] emitting the saved application or a [net.plshark.errors.DuplicateException] if an application
     * with the same name already exists
     */
    fun create(application: Application): Mono<Application>

    /**
     * Delete an application
     * @param name the application name
     * @return a [Mono] signalling when complete
     */
    fun deleteByName(name: String): Mono<Void>
}
