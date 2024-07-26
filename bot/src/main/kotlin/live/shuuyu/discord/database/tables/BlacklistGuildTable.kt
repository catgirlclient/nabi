package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object BlacklistGuildTable: LongIdTable() {
    val guildId = long("guild_id")
    val reason = text("reason").nullable()
    val timestamp = long("timestamp")
    val ownerId = long("owner_id")
}