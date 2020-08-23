package net.plshark.users.service

import net.plshark.users.model.Role
import net.plshark.users.model.RoleCreate
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Service for managing roles
 */
interface RolesService {

    /**
     * Retrieve a role by ID
     * @param roleId the role ID
     * @return a [Mono] emitting the matching role or empty if not found
     */
    fun findById(roleId: Long): Mono<Role>

    /**
     * Retrieve a role by ID
     * @param roleId the role ID
     * @return ta [Mono] emitting he matching role or an [net.plshark.errors.ObjectNotFoundException] if not found
     */
    fun findRequiredById(roleId: Long): Mono<Role>

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return a [Flux] emitting the roles
     */
    fun getRoles(maxResults: Int, offset: Long): Flux<Role>

    /**
     * Save a new role
     * @param role the role
     * @return a [Mono] emitting the saved role or a [net.plshark.errors.DuplicateException] if a role with the same
     * name already exists in the same application
     */
    fun create(role: RoleCreate): Mono<Role>

    /**
     * Delete a role
     * @param roleId the role ID
     * @return a [Mono] signalling when complete
     */
    fun delete(roleId: Long): Mono<Void>
}
