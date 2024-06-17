package live.shuuyu.discord.database.utils

import live.shuuyu.discord.database.NabiDatabaseCore
import live.shuuyu.discord.database.config.GuildSettingsConfig
import live.shuuyu.discord.database.config.ModLoggingConfig
import live.shuuyu.discord.database.config.PhishingConfig
import live.shuuyu.discord.database.tables.GuildSettingsTable
import live.shuuyu.discord.database.tables.ModLoggingTable
import live.shuuyu.discord.database.tables.PhishingTable
import org.jetbrains.exposed.sql.selectAll

class GuildDatabaseUtils(val database: NabiDatabaseCore) {
    suspend fun getGuildConfig(guildId: Long) = database.asyncSuspendableTransaction {
        GuildSettingsTable.selectAll().where {
            GuildSettingsTable.guildId eq guildId
        }.limit(1).map {
            GuildSettingsConfig(
                it[GuildSettingsTable.guildId],
                it[GuildSettingsTable.prefix],
                it[GuildSettingsTable.welcomeChannel],
                it[GuildSettingsTable.leaveChannel],
                it[GuildSettingsTable.modConfig]?.value,
                it[GuildSettingsTable.phishingConfig]?.value
            )
        }.firstOrNull()
    }.await()

    suspend fun getModLoggingConfig(modLoggingConfigId: Long?) = database.asyncSuspendableTransaction {
        ModLoggingTable.selectAll().where {
            ModLoggingTable.id eq modLoggingConfigId
        }.limit(1).map {
            ModLoggingConfig(
                it[ModLoggingTable.enabled],
                it[ModLoggingTable.channelId],
                it[ModLoggingTable.sendMessageToChannel],
                it[ModLoggingTable.logUserBans],
                it[ModLoggingTable.logUserMutes],
                it[ModLoggingTable.logUserWarns],
                it[ModLoggingTable.logMessageDelete],
                it[ModLoggingTable.logMessageModify],
                it[ModLoggingTable.silentFail]
            )
        }.firstOrNull()
    }.await()

    suspend fun getPhishingConfig(phishingConfigId: Long?) = database.asyncSuspendableTransaction {
        PhishingTable.selectAll().where {
            PhishingTable.id eq phishingConfigId
        }.limit(1).map {
            PhishingConfig(
                it[PhishingTable.enabled],
                it[PhishingTable.channelId],
                it[PhishingTable.sendMessagesToChannel],
                it[PhishingTable.sendPunishmentToDm],
                it[PhishingTable.punishmentType],
                it[PhishingTable.silentFail]
            )
        }.firstOrNull()
    }.await()
}