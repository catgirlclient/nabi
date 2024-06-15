package live.shuuyu.discord.database.entity

import live.shuuyu.discord.database.tables.WarnTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class WarnEntity(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<WarnEntity>(WarnTable)

    var userId by WarnTable.userId
    var executorId by WarnTable.executorId
    var guildId by WarnTable.guildId
    var reason by WarnTable.reason
    var timestamp by WarnTable.timestamp
}