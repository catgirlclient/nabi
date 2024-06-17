package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object GuildSettingsTable: LongIdTable() {
    val guildId = long("guildid").index()
    val prefix = text("prefix")
    val welcomeChannel = long("welcome_channel").nullable()
    val leaveChannel = long("leave_channel").nullable()
    val modConfig = optReference("moderation_config", ModLoggingTable, onDelete = ReferenceOption.CASCADE)
    val phishingConfig = optReference("phishing_config", PhishingTable, onDelete = ReferenceOption.CASCADE)
    val warnPunishmentType = integer("warnPunishmentType").default(1)
}

