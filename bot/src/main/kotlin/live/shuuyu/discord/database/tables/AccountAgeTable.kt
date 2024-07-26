package live.shuuyu.discord.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable
import kotlin.time.Duration.Companion.days

object AccountAgeTable: LongIdTable() {
    val enabled = bool("enabled").default(false)
    val minAccountAge = long("minimum_account_age").default(7.days.inWholeDays)
    val silentFail = bool("silent_fail").default(true)
}