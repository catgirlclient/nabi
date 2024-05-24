package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.IdTable

object LoggingChannel: IdTable<Long>("logging_channel") {
    override val id = long("guildId").entityId()

    val moderation = long("moderation").index()
    val welcome = long("welcome").index()
    val leave = long("leave").index()

    override val primaryKey = PrimaryKey(id)
}