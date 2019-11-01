package net.plshark.users.repo;

import net.plshark.users.model.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository for saving, deleting, and retrieving roles
 */
public interface RolesRepository {

    /**
     * Get a role by ID
     * @param id the ID
     * @return the matching role
     */
    Mono<Role> get(long id);

    /**
     * Get a role by name
     * @param applicationId the application the role belongs to
     * @param name the role name
     * @return the matching role
     */
    Mono<Role> get(long applicationId, String name);

    /**
     * Get all roles up to the maximum result count and starting at an offset
     * @param maxResults the maximum number of results to return
     * @param offset the offset to start the list at, 0 to start at the beginning
     * @return the roles
     */
    Flux<Role> getRoles(int maxResults, long offset);

    /**
     * Insert a new role
     * @param role the role to insert
     * @return the inserted role, will have the ID set
     */
    Mono<Role> insert(Role role);

    /**
     * Delete a role by ID
     * @param id the role ID
     * @return an empty result
     */
    Mono<Void> delete(long id);

    /**
     * Get all roles belonging to an application
     * @param applicationId the application ID
     * @return the roles
     */
    Flux<Role> getRolesForApplication(long applicationId);
}
