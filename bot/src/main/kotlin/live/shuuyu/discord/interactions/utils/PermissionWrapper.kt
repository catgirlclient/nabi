package live.shuuyu.discord.interactions.utils

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions

@JvmInline
value class PermissionWrapper(val permissions: Permissions) {
    suspend fun hasPermission(vararg permission: Permission): Boolean {


        return permission.all { it in permissions }
    }
}