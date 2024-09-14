package live.shuuyu.nabi.database.tables.guild

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object GuildSettingsTable: LongIdTable() {
    val guildId = long("guild_id").index()
    val accountAgeSettings = optReference("account_age", AccountAgeSettingsTable, onDelete = ReferenceOption.CASCADE)
    val loggingSettings = optReference("logging_settings", LoggingSettingsTable, onDelete =  ReferenceOption.CASCADE)
    val phishingSettings = optReference("phishing_settings", PhishingSettingsTable, onDelete = ReferenceOption.CASCADE)
    val showPunishmentMessage = bool("show_punishment_message").default(true)
}