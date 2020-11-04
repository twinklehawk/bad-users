package net.plshark.users.repo.springdata

import io.r2dbc.spi.Row
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import net.plshark.users.model.Group
import net.plshark.users.model.GroupCreate
import net.plshark.users.repo.GroupsRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.await
import org.springframework.data.r2dbc.core.awaitOneOrNull
import org.springframework.stereotype.Repository

/**
 * Groups repository using spring data
 */
@Repository
class SpringDataGroupsRepository(private val client: DatabaseClient) : GroupsRepository {

    override suspend fun findById(id: Long): Group? {
        return client.execute("SELECT * FROM groups WHERE id = :id")
            .bind("id", id)
            .map { row: Row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override suspend fun findByName(name: String): Group? {
        return client.execute("SELECT * FROM groups WHERE name = :name")
            .bind("name", name)
            .map { row -> mapRow(row) }
            .awaitOneOrNull()
    }

    override fun getGroups(maxResults: Int, offset: Long): Flow<Group> {
        require(maxResults >= 1) { "Max results must be greater than 0" }
        require(offset >= 0) { "Offset cannot be negative" }
        val sql = "SELECT * FROM groups ORDER BY id OFFSET $offset ROWS FETCH FIRST $maxResults ROWS ONLY"
        return client.execute(sql)
            .map { row -> mapRow(row) }
            .all()
            .asFlow()
    }

    override suspend fun insert(group: GroupCreate): Group {
        val id = client.execute("INSERT INTO groups (name) VALUES (:name) RETURNING id")
            .bind("name", group.name)
            .fetch().one()
            .map { it["id"] as Long? ?: throw IllegalStateException("No ID returned from insert") }
            .awaitSingle()
        return Group(id = id, name = group.name)
    }

    override suspend fun deleteById(groupId: Long) {
        return client.execute("DELETE FROM groups WHERE id = :id")
            .bind("id", groupId)
            .await()
    }

    companion object {
        /**
         * Map a database row to a [Group]
         * @param row the database row
         * @return the mapped group
         */
        fun mapRow(row: Row): Group {
            return Group(
                id = row["id", java.lang.Long::class.java]!!.toLong(),
                name = row["name", String::class.java]!!
            )
        }
    }
}
