package live.shuuyu.nabi.database.tables.guild

import org.jetbrains.exposed.dao.id.LongIdTable
import kotlin.time.Duration.Companion.days

object AccountAgeSettingsTable: LongIdTable() {
    val enabled = bool("enabled").default(false)
    val minAccountAge = long("min_account_age").default(7.days.inWholeDays)
}