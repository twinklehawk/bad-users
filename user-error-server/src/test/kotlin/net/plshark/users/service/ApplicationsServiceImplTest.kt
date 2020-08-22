package net.plshark.users.service

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.DuplicateException
import net.plshark.users.model.Application
import net.plshark.users.model.Role
import net.plshark.users.repo.ApplicationRepository
import net.plshark.users.repo.RolesRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.dao.DataIntegrityViolationException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class ApplicationsServiceImplTest {

    private val appRepo = mockk<ApplicationRepository>()
    private val rolesRepo = mockk<RolesRepository>()
    private val service = ApplicationsServiceImpl(appRepo, rolesRepo)

    @Test
    fun `get should pass through the response from the repo`() {
        val app = Application(1, "app")
        every { appRepo.findByName("app") } returns Mono.just(app)

        StepVerifier.create(service.findByName("app"))
                .expectNext(app)
                .verifyComplete()
    }

    @Test
    fun `create should pass through the response from the repo`() {
        val request = Application(name = "app")
        val inserted = Application(1, "app")
        every { appRepo.save(request) } returns Mono.just(inserted)

        StepVerifier.create(service.create(request))
                .expectNext(inserted)
                .verifyComplete()
    }

    @Test
    fun `create should map the exception for a duplicate name to a DuplicateException`() {
        val request = Application(name = "app")
        every { appRepo.save(request) } returns Mono.error(DataIntegrityViolationException("test error"))

        StepVerifier.create(service.create(request))
                .verifyError(DuplicateException::class.java)
    }

    @Test
    fun `create should fail if the application has an ID not equal to 0`() {
        assertThrows<IllegalArgumentException> { service.create(Application(1, "app")) }
    }

    @Test
    fun `delete should delete the app`() {
        val deleteAppProbe = PublisherProbe.empty<Void>()
        every { appRepo.deleteByName("app") } returns deleteAppProbe.mono()

        StepVerifier.create(service.deleteByName("app"))
                .verifyComplete()
        deleteAppProbe.assertWasSubscribed()
    }

    @Test
    fun `getApplicationRoles should pass through the response from the repo`() {
        val role1 = Role(1, 100, "role1")
        val role2 = Role(2, 100, "role2")
        every { rolesRepo.getRolesForApplication(100) } returns Flux.just(role1, role2)

        StepVerifier.create(service.getApplicationRoles(100))
                .expectNext(role1, role2)
                .verifyComplete()
    }
}
