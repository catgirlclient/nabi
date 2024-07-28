package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object GuildSettingsTable: LongIdTable() {
    val guildId = long("guildid").index()
    val prefix = text("prefix")
    val welcomeConfig = optReference("welcome_config", WelcomeChannelTable, onDelete = ReferenceOption.CASCADE)
    val leaveConfig = optReference("leave_config", LeaveChannelTable, onDelete = ReferenceOption.CASCADE)
    val modLoggingConfig = optReference("moderation_config", ModLoggingTable, onDelete = ReferenceOption.CASCADE)
    val phishingConfig = optReference("phishing_config", PhishingTable, onDelete = ReferenceOption.CASCADE)
    val accountAgeConfig = optReference("account_age_config", AccountAgeTable, onDelete = ReferenceOption.CASCADE)
    val preconfiguredBanReason = text("preconfigured_ban_reason").default("No reason provided.")
}

