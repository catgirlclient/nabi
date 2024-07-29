package live.shuuyu.discord.database.utils

import live.shuuyu.discord.database.NabiDatabaseCore
import live.shuuyu.discord.database.config.*
import live.shuuyu.discord.database.tables.*
import org.jetbrains.exposed.sql.selectAll

class GuildDatabaseUtils(val database: NabiDatabaseCore) {
    suspend fun getGuildConfig(guildId: Long) = database.asyncSuspendableTransaction {
        GuildSettingsTable.selectAll().where {
            GuildSettingsTable.guildId eq guildId
        }.limit(1).map {
            GuildSettingsConfig(
                it[GuildSettingsTable.guildId],
                it[GuildSettingsTable.prefix],
                it[GuildSettingsTable.welcomeConfig]?.value,
                it[GuildSettingsTable.leaveConfig]?.value,
                it[GuildSettingsTable.modLoggingConfig]?.value,
                it[GuildSettingsTable.phishingConfig]?.value,
                it[GuildSettingsTable.accountAgeConfig]?.value,
                it[GuildSettingsTable.preconfiguredBanReason]
            )
        }.firstOrNull()
    }.await()

    suspend fun getAccountAgeConfig(accountAgeConfigId: Long?) = database.asyncSuspendableTransaction {
        AccountAgeTable.selectAll().where {
            AccountAgeTable.id eq accountAgeConfigId
        }.limit(1).map {
            AccountAgeConfig(
                it[AccountAgeTable.enabled],
                it[AccountAgeTable.minAccountAge],
                it[AccountAgeTable.silentFail]
            )
        }.firstOrNull()
    }.await()

    suspend fun getLeaveChannelConfig(leaveChannelConfigId: Long?) = database.asyncSuspendableTransaction {
        LeaveChannelTable.selectAll().where {
            LeaveChannelTable.id eq leaveChannelConfigId
        }.limit(1).map {
            LeaveChannelConfig(
                it[LeaveChannelTable.enabled],
                it[LeaveChannelTable.channelId],
                it[LeaveChannelTable.customMessage],
                it[LeaveChannelTable.silentFail]
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
                it[ModLoggingTable.logUserUnbans],
                it[ModLoggingTable.logUserKicks],
                it[ModLoggingTable.logUserMutes],
                it[ModLoggingTable.logUserUnmutes],
                it[ModLoggingTable.logUserWarns],
                it[ModLoggingTable.logUserWarnRemoves],
                it[ModLoggingTable.logChannelSlowmode],
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
                it[PhishingTable.silentFail],
                it[PhishingTable.defaultMuteDuration],
                it[PhishingTable.defaultSoftBanDuration]
            )
        }.firstOrNull()
    }.await()

    suspend fun getWelcomeChannelConfig(welcomeChannelConfigId: Long?) = database.asyncSuspendableTransaction {
        WelcomeChannelTable.selectAll().where {
            WelcomeChannelTable.id eq welcomeChannelConfigId
        }.limit(1).map {
            WelcomeChannelConfig (
                it[WelcomeChannelTable.enabled],
                it[WelcomeChannelTable.channelId],
                it[WelcomeChannelTable.customMessage],
                it[WelcomeChannelTable.silentFail]
            )
        }.firstOrNull()
    }.await()
}