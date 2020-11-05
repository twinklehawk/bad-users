package net.plshark.users.repo.springdata

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import net.plshark.users.model.Group
import net.plshark.users.model.Role
import net.plshark.users.repo.UserGroupsRepository
import org.springframework.data.r2dbc.core.DatabaseClient
import org.springframework.data.r2dbc.core.await
import org.springframework.stereotype.Repository

/**
 * User groups repository using spring data
 */
@Repository
class SpringDataUserGroupsRepository(private val client: DatabaseClient) : UserGroupsRepository {

    override fun findGroupsByUserId(userId: Long): Flow<Group> {
        return client.execute(
            "SELECT * FROM groups g INNER JOIN user_groups ug ON g.id = ug.group_id WHERE ug.user_id = :id"
        )
            .bind("id", userId)
            .map { row -> SpringDataGroupsRepository.mapRow(row) }
            .all()
            .asFlow()
    }

    override fun findGroupRolesByUserId(userId: Long): Flow<Role> {
        return client.execute(
            "SELECT r.* from roles r, user_groups ug, group_roles gr WHERE ug.user_id = :userId AND gr.group_id " +
                    "= ug.group_id AND r.id = gr.role_id"
        )
            .bind("userId", userId)
            .map { row -> SpringDataRolesRepository.mapRow(row) }
            .all()
            .asFlow()
    }

    override suspend fun insert(userId: Long, groupId: Long) {
        return client.execute("INSERT INTO user_groups (user_id, group_id) values (:userId, :groupId)")
            .bind("userId", userId)
            .bind("groupId", groupId)
            .await()
    }

    override suspend fun deleteById(userId: Long, groupId: Long) {
        return client.execute("DELETE FROM user_groups WHERE user_id = :userId AND group_id = :groupId")
            .bind("userId", userId)
            .bind("groupId", groupId)
            .await()
    }
}
