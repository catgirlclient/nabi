package live.shuuyu.nabi.database.tables.guild

import org.jetbrains.exposed.dao.id.LongIdTable

object WelcomeChannelSettingsTable: LongIdTable() {
    val enabled = bool("enabled").default(false)
    val channelId = long("channel_id").nullable().default(null)
}