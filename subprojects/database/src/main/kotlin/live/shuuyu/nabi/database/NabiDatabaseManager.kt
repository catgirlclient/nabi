package live.shuuyu.nabi.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import com.zaxxer.hikari.util.IsolationLevel
import live.shuuyu.nabi.database.tables.guild.GuildSettingsTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class NabiDatabaseManager(val config: NabiDatabaseConfig) {
    companion object {
        const val DRIVER_CLASS_NAME = "org.postgresql.Driver"

        fun initialize(
            jdbcAddress: String,
            jdbcUsername: String,
            jdbcPassword: String
        ): Database = Database.connect(
            datasource = HikariDataSource(HikariConfig().apply {
                jdbcUrl = jdbcAddress
                username = jdbcUsername
                password = jdbcPassword
                driverClassName = DRIVER_CLASS_NAME
                isAutoCommit = false
                transactionIsolation = IsolationLevel.TRANSACTION_REPEATABLE_READ.name
                metricsTrackerFactory = PrometheusMetricsTrackerFactory()
            }),
            databaseConfig = DatabaseConfig.invoke {
                defaultMaxAttempts = 10
            }
        )
    }

    suspend fun createMissingTablesAndColums() = newSuspendedTransaction {
        SchemaUtils.createMissingTablesAndColumns(
            GuildSettingsTable
        )
    }
}