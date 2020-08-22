package net.plshark.users.service

import net.plshark.errors.DuplicateException
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import net.plshark.users.repo.RolesRepository
import net.plshark.users.repo.ApplicationRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Applications management service implementation
 */
@Component
class ApplicationsServiceImpl(private val appRepo: ApplicationRepository, private val rolesRepo: RolesRepository) :
    ApplicationsService {

    override fun findByName(name: String): Mono<Application> {
        return appRepo.findByName(name)
    }

    override fun findAll(): Flux<Application> {
        return appRepo.findAll()
    }

    override fun create(application: Application): Mono<Application> {
        require(application.id == 0L) { "ID must be 0 when creating an application" }
        return appRepo.save(application)
            .onErrorMap(DataIntegrityViolationException::class.java) { e: DataIntegrityViolationException ->
                DuplicateException("An application with name ${application.name} already exists", e)
            }
    }

    override fun deleteByName(name: String): Mono<Void> {
        return appRepo.deleteByName(name)
    }

    fun getApplicationRoles(id: Long): Flux<Role> {
        return rolesRepo.getRolesForApplication(id)
    }
}
