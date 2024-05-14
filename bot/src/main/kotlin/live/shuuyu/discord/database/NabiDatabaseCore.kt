package live.shuuyu.discord.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import com.zaxxer.hikari.util.IsolationLevel
import live.shuuyu.discord.database.tables.BlacklistUser
import live.shuuyu.discord.utils.config.DatabaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.DatabaseConfig as ExposedDatabaseConfig
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class NabiDatabaseCore(private val config: DatabaseConfig) {
    companion object {
        private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
    }

    fun initialize(): Database = Database.connect(
        datasource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = config.url
            driverClassName = DRIVER_CLASS_NAME
            username = config.username
            password = config.password
            transactionIsolation = IsolationLevel.TRANSACTION_REPEATABLE_READ.name
            metricsTrackerFactory = PrometheusMetricsTrackerFactory()
        }),
        databaseConfig = ExposedDatabaseConfig.invoke {
            defaultRepetitionAttempts = 5
        }
    )

    suspend fun createMissingSchemaAndColumns() {
        newSuspendedTransaction {
            SchemaUtils.createMissingTablesAndColumns(
                BlacklistUser
            )
        }
    }
}