package live.shuuyu.discord.database.entity

import live.shuuyu.discord.database.tables.GuildSettingsTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class GuildSettingsEntity(id: EntityID<Long>): LongEntity(id) {
    companion object: LongEntityClass<GuildSettingsEntity>(GuildSettingsTable)

    var guildId by GuildSettingsTable.guildId
    var prefix by GuildSettingsTable.prefix
    var welcomeChannel by GuildSettingsTable.welcomeChannel
    var leaveChannel by GuildSettingsTable.leaveChannel
    var modConfig by GuildSettingsTable.modConfig
    val phishingConfig by GuildSettingsTable.phishingConfig
    var warnPunishmentType by GuildSettingsTable.warnPunishmentType
}