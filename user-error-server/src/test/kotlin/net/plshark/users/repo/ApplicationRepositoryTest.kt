package net.plshark.users.repo

import net.plshark.testutils.DbIntTest
import net.plshark.users.model.Application
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestConfig::class])
class ApplicationRepositoryTest : DbIntTest() {

    @Autowired
    lateinit var repo: ApplicationRepository

    @Test
    fun `inserting an application returns the inserted application with the ID set`() {
        val inserted = repo.save(Application(name = "app")).block()

        assertNotNull(inserted?.id)
        assertEquals("app", inserted?.name)
    }

    @Test
    fun `can retrieve a previously inserted application by ID`() {
        val inserted = repo.save(Application(name = "test-app")).block()!!

        val app = repo.findById(inserted.id).block()

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by ID when no application matches returns empty`() {
        StepVerifier.create(repo.findById(1000))
                .verifyComplete()
    }

    @Test
    fun `can retrieve a previously inserted application by name`() {
        val inserted = repo.save(Application(name = "test-app")).block()!!

        val app = repo.findByName(inserted.name).block()

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by name when no application matches returns empty`() {
        StepVerifier.create(repo.findByName("app"))
                .verifyComplete()
    }

    @Test
    fun `can delete a previously inserted application by ID`() {
        val inserted = repo.save(Application(name = "test-app")).block()!!

        repo.deleteById(inserted.id).block()
        val retrieved = repo.findById(inserted.id).block()

        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete an application by ID that does not exist`() {
        repo.deleteById(10000).block()
    }

    @Test
    fun `can delete a previously inserted application by name`() {
        val inserted = repo.save(Application(name = "test-app")).block()!!

        repo.deleteByName(inserted.name).block()
        val retrieved = repo.findById(inserted.id).block()

        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete an application by name that does not exist`() {
        repo.deleteByName("test").block()
    }
}
