package live.shuuyu.nabi.database.tables.guild

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import kotlin.time.Duration.Companion.days

public object AccountAgeSettingsTable: LongIdTable() {
    public val enabled: Column<Boolean> = bool("enabled").default(false)
    public val minAccountAge: Column<Long> = long("min_account_age").default(7.days.inWholeDays)
}