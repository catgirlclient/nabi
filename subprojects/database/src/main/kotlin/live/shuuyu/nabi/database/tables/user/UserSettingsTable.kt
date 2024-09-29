package live.shuuyu.nabi.database.tables.user

import org.jetbrains.exposed.dao.id.LongIdTable

object UserSettingsTable: LongIdTable() {
    val userId = long("user_id").index()
}