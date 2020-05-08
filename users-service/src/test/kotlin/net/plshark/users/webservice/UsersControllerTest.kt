package net.plshark.users.webservice

import io.mockk.every
import io.mockk.mockk
import net.plshark.errors.BadRequestException
import net.plshark.users.model.PasswordChangeRequest
import net.plshark.users.model.RoleGrant
import net.plshark.users.model.User
import net.plshark.users.service.UsersService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.test.publisher.PublisherProbe

class UsersControllerTest {

    private val service = mockk<UsersService>()
    private val controller = UsersController(service)

    @Test
    fun `delete passes the user ID through to be deleted`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.delete("user") } returns probe.mono()

        StepVerifier.create(controller.delete("user"))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `change password passes the user ID, current password, and new passwords through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.updateUserPassword("bob", "current", "new") } returns probe.mono()

        StepVerifier.create(controller.changePassword("bob", PasswordChangeRequest("current", "new")))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `granting a role passes the user and role names through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.grantRoleToUser("user", "test-app", "role1") } returns probe.mono()

        StepVerifier.create(controller.grantRole("user", RoleGrant("test-app", "role1")))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `removing a role passes the user and role names through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.removeRoleFromUser("ted", "app", "role") } returns probe.mono()

        StepVerifier.create(controller.removeRole("ted", "app", "role"))
            .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `granting a group passes the user and group names through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.grantGroupToUser("user", "group") } returns probe.mono()

        StepVerifier.create(controller.grantGroup("user", "group"))
                .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `removing a group passes the user and role names through`() {
        val probe = PublisherProbe.empty<Void>()
        every { service.removeGroupFromUser("ted", "group") } returns probe.mono()

        StepVerifier.create(controller.removeGroup("ted", "group"))
                .verifyComplete()
        probe.assertWasSubscribed()
        probe.assertWasRequested()
        probe.assertWasNotCancelled()
    }

    @Test
    fun `getUsers passes the max results and offset through`() {
        val user1 = User(1, "user", null)
        val user2 = User(2, "user2", null)
        every { service.getUsers(3, 2) } returns Flux.just(user1, user2)

        StepVerifier.create(controller.getUsers(3, 2))
                .expectNext(user1, user2)
                .verifyComplete()
    }

    @Test
    fun `getUser passes the username through`() {
        val user1 = User(1, "user", null)
        every { service.get("user") } returns Mono.just(user1)

        StepVerifier.create(controller.getUser("user"))
                .expectNext(user1)
                .verifyComplete()
    }

    @Test
    fun `insert passes through the response from the service`() {
        val request = User(null, "user", "test-pass")
        val created = User(1, "user", null)
        every { service.create(request) } returns Mono.just(created)

        StepVerifier.create(controller.create(request))
                .expectNext(created)
                .verifyComplete()
    }

    @Test
    fun `an insert request is rejected if the password is empty`() {
        val request = User(null, "user", null)

        assertThrows<BadRequestException> { controller.create(request) }
    }
}