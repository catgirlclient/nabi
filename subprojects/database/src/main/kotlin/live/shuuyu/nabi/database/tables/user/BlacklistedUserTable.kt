package live.shuuyu.nabi.database.tables.user

import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column

public object BlacklistedUserTable: LongIdTable() {
    public val userId: Column<Long> = long("user_id")
    public val developerId: Column<Long> = long("developer_id")
    public val reason: Column<String?> = text("reason").nullable()
    public val timestamp: Column<Long> = long("timestamp")

    /**
     * Expiration date of the blacklist. This is because if the elapsed time expires, it should invalidate the table.
     * If it's indefinite, this value should return null, and return a message saying they've been blacklisted indefinitely.
     */
    public val expirationDate: Column<Long?> = long("expiration_date").nullable()
}