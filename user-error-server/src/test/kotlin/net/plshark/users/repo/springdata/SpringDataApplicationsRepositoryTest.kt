package net.plshark.users.repo.springdata

import io.r2dbc.spi.ConnectionFactories
import kotlinx.coroutines.runBlocking
import net.plshark.testutils.DbIntTest
import net.plshark.users.model.ApplicationCreate
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.r2dbc.core.DatabaseClient

class SpringDataApplicationsRepositoryTest : DbIntTest() {

    lateinit var repo: SpringDataApplicationsRepository

    @BeforeEach
    fun setup() {
        val connectionFactory = ConnectionFactories.get(DB_URL)
        val db = DatabaseClient.create(connectionFactory)
        repo = SpringDataApplicationsRepository(db)
    }

    @Test
    fun `inserting an application returns the inserted application with the ID set`() = runBlocking {
        val inserted = repo.insert(ApplicationCreate("app"))

        assertNotNull(inserted.id)
        assertEquals("app", inserted.name)
    }

    @Test
    fun `can retrieve a previously inserted application by ID`() = runBlocking {
        val inserted = repo.insert(ApplicationCreate("test-app"))

        val app = repo.findById(inserted.id)

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by ID when no application matches returns empty`() = runBlocking {
        assertNull(repo.findById(1000))
    }

    @Test
    fun `can retrieve a previously inserted application by name`() = runBlocking {
        val inserted = repo.insert(ApplicationCreate("test-app"))

        val app = repo.findByName(inserted.name)

        assertEquals(inserted, app)
    }

    @Test
    fun `retrieving an application by name when no application matches returns empty`() = runBlocking {
        assertNull(repo.findByName("app"))
    }

    @Test
    fun `can delete a previously inserted application by ID`() = runBlocking {
        val inserted = repo.insert(ApplicationCreate("test-app"))

        repo.deleteById(inserted.id)
        val retrieved = repo.findById(inserted.id)

        assertNull(retrieved)
    }

    @Test
    fun `no exception is thrown when attempting to delete an application by ID that does not exist`() = runBlocking {
        repo.deleteById(10000)
    }
}
