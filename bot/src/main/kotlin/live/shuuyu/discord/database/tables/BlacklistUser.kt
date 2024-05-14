package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.IdTable

object BlacklistUser: IdTable<Long>("blacklist_user") {
    override val id = long("user").entityId()

    val reason = text("reason").nullable()
    val time = long("blacklist_time")


    override val primaryKey = PrimaryKey(id)
}