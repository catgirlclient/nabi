package live.shuuyu.nabi.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import com.zaxxer.hikari.util.IsolationLevel
import live.shuuyu.nabi.database.manager.GuildManager
import live.shuuyu.nabi.database.manager.MemberManager
import live.shuuyu.nabi.database.manager.UserManager
import live.shuuyu.nabi.database.tables.guild.GuildSettingsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

public class NabiDatabaseManager(public val config: NabiDatabaseConfig) {
    public companion object {
        private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
    }

    public val guild: GuildManager = GuildManager(this)
    public val member: MemberManager = MemberManager(this)
    public val user: UserManager = UserManager(this)

    public fun initialize(): Database = Database.connect(
        datasource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = config.jdbcUrl
            username = config.jdbcUsername
            password = config.jdbcPassword
            driverClassName = DRIVER_CLASS_NAME
            isAutoCommit = false
            transactionIsolation = IsolationLevel.TRANSACTION_REPEATABLE_READ.name
            metricsTrackerFactory = PrometheusMetricsTrackerFactory()
        }),
        databaseConfig = DatabaseConfig.invoke {
            defaultMaxAttempts = 10
        }
    )

    public suspend fun createMissingTablesAndColums(): Unit = newSuspendedTransaction {
        SchemaUtils.createMissingTablesAndColumns(
            GuildSettingsTable
        )
    }
}