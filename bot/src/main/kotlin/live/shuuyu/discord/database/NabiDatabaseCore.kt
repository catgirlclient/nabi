package live.shuuyu.discord.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import com.zaxxer.hikari.util.IsolationLevel
import live.shuuyu.discord.NabiCore
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class NabiDatabaseCore(private val nabi: NabiCore) {
    companion object {
        private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
    }

    fun initialize(): Database = Database.connect(
        datasource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = nabi.config.jdbcUrl
            driverClassName = DRIVER_CLASS_NAME
            username = nabi.config.jdbcUsername
            password = nabi.config.jdbcPassword
            transactionIsolation = IsolationLevel.TRANSACTION_REPEATABLE_READ.name
            metricsTrackerFactory = PrometheusMetricsTrackerFactory()
        }),
        databaseConfig = DatabaseConfig.invoke {
            defaultRepetitionAttempts = 5
        }
    )

    suspend fun createMissingSchemaAndColumns() {
        newSuspendedTransaction {

        }
    }
}