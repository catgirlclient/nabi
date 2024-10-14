package live.shuuyu.nabi.database.manager

import live.shuuyu.nabi.database.NabiDatabaseManager
import live.shuuyu.nabi.database.config.guild.*
import live.shuuyu.nabi.database.tables.guild.*
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync

public class GuildManager(database: NabiDatabaseManager){
    public suspend fun getAccountAgeSettingsConfig(
        accountAgeSettingsId: Long?
    ): AccountAgeSettingsConfig? = suspendedTransactionAsync {
        AccountAgeSettingsTable.selectAll().where {
            AccountAgeSettingsTable.id eq accountAgeSettingsId
        }.limit(1).map {
            AccountAgeSettingsConfig (
                it[AccountAgeSettingsTable.enabled],
                it[AccountAgeSettingsTable.minAccountAge]
            )
        }.firstOrNull()
    }.await()

    public suspend fun getGuildSettingsConfig(
        guildId: Long
    ): GuildSettingsConfig? = suspendedTransactionAsync {
        GuildSettingsTable.selectAll().where {
            GuildSettingsTable.guildId eq guildId
        }.limit(1).map {
            GuildSettingsConfig (
                it[GuildSettingsTable.guildId],
                it[GuildSettingsTable.defaultLanguage],
                it[GuildSettingsTable.muteRoleId],
                it[GuildSettingsTable.accountAgeSettings]?.value,
                it[GuildSettingsTable.loggingSettings]?.value,
                it[GuildSettingsTable.phishingSettings]?.value,
                it[GuildSettingsTable.welcomeSettings]?.value,
                it[GuildSettingsTable.leaveSettings]?.value,
                it[GuildSettingsTable.showPunishmentMessage],
                it[GuildSettingsTable.directMessageUser]
            )
        }.firstOrNull()
    }.await()

    public suspend fun getLeaveChannelSettingsConfig(
        leaveChannelConfigId: Long?
    ): LeaveChannelSettingsConfig? = suspendedTransactionAsync {
        LeaveChannelSettingsTable.selectAll().where {
            LeaveChannelSettingsTable.id eq leaveChannelConfigId
        }.limit(1).map {
            LeaveChannelSettingsConfig (
                it[LeaveChannelSettingsTable.enabled],
                it[LeaveChannelSettingsTable.channelId]
            )
        }.firstOrNull()
    }.await()

    public suspend fun getLoggingSettingsConfig(
        loggingConfigId: Long?
    ): LoggingSettingsConfig? = suspendedTransactionAsync {
        LoggingSettingsTable.selectAll().where {
            LoggingSettingsTable.id eq loggingConfigId
        }.limit(1).map {
            LoggingSettingsConfig (
                it[LoggingSettingsTable.enabled],
                it[LoggingSettingsTable.channelId],
                it[LoggingSettingsTable.logUserBans],
                it[LoggingSettingsTable.logUserUnbans],
                it[LoggingSettingsTable.logUserKicks],
                it[LoggingSettingsTable.logUserMutes],
                it[LoggingSettingsTable.logUserUnmutes],
                it[LoggingSettingsTable.logUserWarns],
                it[LoggingSettingsTable.logUserWarnsRemove],
                it[LoggingSettingsTable.logChannelSlowmodes],
                it[LoggingSettingsTable.logMessageDelete],
                it[LoggingSettingsTable.logMessageModify]
            )
        }.firstOrNull()
    }.await()

    public suspend fun getPhishingSettingsConfig(
        phishingSettingsConfigId: Long?
    ): PhishingSettingsConfig? = suspendedTransactionAsync {
        PhishingSettingsTable.selectAll().where {
            PhishingSettingsTable.id eq phishingSettingsConfigId
        }.limit(1).map {
            PhishingSettingsConfig (
                it[PhishingSettingsTable.enabled],
                it[PhishingSettingsTable.channelId],
                it[PhishingSettingsTable.sendMessageToChannel],
                it[PhishingSettingsTable.punishmentType],
                it[PhishingSettingsTable.defaultMuteDuration],
                it[PhishingSettingsTable.whitelistedChannels]
            )
        }.firstOrNull()
    }.await()

    public suspend fun getWelcomeChannelSettingsConfig(
        welcomeChannelConfigId: Long?
    ): WelcomeChannelSettingsConfig? = suspendedTransactionAsync {
        WelcomeChannelSettingsTable.selectAll().where {
            WelcomeChannelSettingsTable.id eq welcomeChannelConfigId
        }.limit(1).map {
            WelcomeChannelSettingsConfig (
                it[WelcomeChannelSettingsTable.enabled],
                it[WelcomeChannelSettingsTable.channelId]
            )
        }.firstOrNull()
    }.await()
}