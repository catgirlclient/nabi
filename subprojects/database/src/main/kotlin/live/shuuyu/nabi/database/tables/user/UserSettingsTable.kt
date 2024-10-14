package live.shuuyu.nabi.database.tables.user

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

public object UserSettingsTable: LongIdTable() {
    public val userId: Column<Long> = long("user_id").index()

    /**
     * Global experience is different from the experience gained from server specific experience.
     */
    public val exp: Column<ULong> = ulong("exp").default(0u)
}