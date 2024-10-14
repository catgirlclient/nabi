package live.shuuyu.nabi.database.tables.guild

import live.shuuyu.common.Language
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ReferenceOption

public object GuildSettingsTable: LongIdTable() {
    public val guildId: Column<Long> = long("guild_id").index()
    public val defaultLanguage: Column<Language> = enumeration<Language>("default_language").default(Language.ENGLISH)
    public val muteRoleId: Column<Long?> = long("mute_role_id").nullable()
    public val accountAgeSettings: Column<EntityID<Long>?> = optReference("account_age", AccountAgeSettingsTable, onDelete = ReferenceOption.CASCADE)
    public val loggingSettings: Column<EntityID<Long>?> = optReference("logging_settings", LoggingSettingsTable, onDelete =  ReferenceOption.CASCADE)
    public val phishingSettings: Column<EntityID<Long>?> = optReference("phishing_settings", PhishingSettingsTable, onDelete = ReferenceOption.CASCADE)
    public val welcomeSettings: Column<EntityID<Long>?> = optReference("welcome_settings", WelcomeChannelSettingsTable, onDelete = ReferenceOption.CASCADE)
    public val leaveSettings: Column<EntityID<Long>?> = optReference("leave_settings", LeaveChannelSettingsTable, onDelete = ReferenceOption.CASCADE)
    public val showPunishmentMessage: Column<Boolean> = bool("show_punishment_message").default(true)
    public val directMessageUser: Column<Boolean> = bool("direct_message_user").default(true)

}