package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object GuildSettingsTable: LongIdTable() {
    val guildId = long("guildid").entityId()
    val prefix = text("prefix").default("?")
    val modChannel = long("mod_logs").nullable()
    val welcomeChannel = long("welcome_channel").nullable()
    val leaveChannel = long("leave_channel").nullable()
    val phishingChannel = optReference("phishing_config", PhishingConfig, onDelete = ReferenceOption.CASCADE)
    val warnPunishmentType = integer("warnPunishmentType").default(1)
}

