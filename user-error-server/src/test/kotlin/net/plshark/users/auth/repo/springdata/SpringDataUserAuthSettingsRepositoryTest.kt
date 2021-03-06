package net.plshark.users.auth.repo.springdata

import kotlinx.coroutines.runBlocking
import net.plshark.testutils.DbTest
import net.plshark.users.auth.model.UserAuthSettings
import net.plshark.users.model.UserCreate
import net.plshark.users.repo.springdata.SpringDataUsersRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.r2dbc.core.DatabaseClient

@DbTest
class SpringDataUserAuthSettingsRepositoryTest {

    private lateinit var repo: SpringDataUserAuthSettingsRepository
    private lateinit var usersRepo: SpringDataUsersRepository

    @BeforeEach
    fun setup(db: DatabaseClient) {
        repo = SpringDataUserAuthSettingsRepository(db)
        usersRepo = SpringDataUsersRepository(db)
    }

    @Test
    fun `inserting settings returns the inserted settings with the ID set`() = runBlocking {
        val user = usersRepo.insert(UserCreate(username = "test-user", password = "test-pass"))
        val inserted = repo.insert(
                UserAuthSettings(
                    id = null,
                    userId = user.id,
                    refreshTokenEnabled = false,
                    refreshTokenExpiration = null,
                    authTokenExpiration = 40
                )
            )

        assertNotNull(inserted.id)
        assertEquals(user.id, inserted.userId)
        assertFalse(inserted.refreshTokenEnabled)
        assertEquals(40, inserted.authTokenExpiration)
        assertNull(inserted.refreshTokenExpiration)
    }

    @Test
    fun `cannot insert settings with an ID already set`() {
        assertThrows<IllegalArgumentException> {
            runBlocking {
                repo.insert(
                    UserAuthSettings(
                        id = 100,
                        userId = 200,
                        refreshTokenEnabled = false,
                        refreshTokenExpiration = null,
                        authTokenExpiration = null
                    )
                )
            }
        }
    }

    @Test
    fun `cannot insert settings without a user ID set`() {
        assertThrows<NullPointerException> {
            runBlocking {
                repo.insert(
                    UserAuthSettings(
                        id = null,
                        userId = null,
                        refreshTokenEnabled = false,
                        refreshTokenExpiration = null,
                        authTokenExpiration = null
                    )
                )
            }
        }
    }

    @Test
    fun `can retrieve previously inserted settings by user ID`() = runBlocking {
        val user = usersRepo.insert(UserCreate(username = "test-user", password = "test-pass"))
        val inserted = repo.insert(UserAuthSettings(
            id = null,
            userId = user.id,
            refreshTokenEnabled = true,
            refreshTokenExpiration = null,
            authTokenExpiration = null
        ))

        assertEquals(inserted, repo.findByUserId(user.id))
    }

    @Test
    fun `retrieving by user ID when no rows match returns empty`() = runBlocking {
        assertNull(repo.findByUserId(1000))
    }

    @Test
    fun `can retrieve previously inserted settings by username`() = runBlocking {
        val user = usersRepo.insert(UserCreate(username = "test-user", password = "test-pass"))
        val inserted = repo.insert(UserAuthSettings(
            id = null,
            userId = user.id,
            refreshTokenEnabled = true,
            refreshTokenExpiration = null,
            authTokenExpiration = null
        ))

        assertEquals(inserted, repo.findByUsername(user.username))
    }

    @Test
    fun `retrieving by username when no rows match returns empty`() = runBlocking {
        assertNull(repo.findByUsername("not a user"))
    }
}
