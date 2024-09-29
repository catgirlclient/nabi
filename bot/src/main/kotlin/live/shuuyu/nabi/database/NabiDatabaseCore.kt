package live.shuuyu.nabi.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.metrics.prometheus.PrometheusMetricsTrackerFactory
import com.zaxxer.hikari.util.IsolationLevel
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import live.shuuyu.nabi.database.tables.BlacklistedUserTable
import live.shuuyu.nabi.database.tables.GuildSettingsTable
import live.shuuyu.nabi.database.tables.WarnTable
import live.shuuyu.nabi.database.utils.GuildDatabaseUtils
import live.shuuyu.nabi.database.utils.MemberDatabaseUtils
import live.shuuyu.nabi.database.utils.UserDatabaseUtils
import live.shuuyu.nabi.utils.config.DatabaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransactionAsync
import kotlin.coroutines.CoroutineContext
import org.jetbrains.exposed.sql.DatabaseConfig as ExposedDatabaseConfig

class NabiDatabaseCore(private val config: DatabaseConfig) {
    companion object {
        private const val DRIVER_CLASS_NAME = "org.postgresql.Driver"
    }

    fun initialize(): Database = Database.connect(
        datasource = HikariDataSource(HikariConfig().apply {
            jdbcUrl = config.address
            driverClassName = DRIVER_CLASS_NAME
            username = config.username
            password = config.password
            isAutoCommit = false
            transactionIsolation = IsolationLevel.TRANSACTION_REPEATABLE_READ.name
            metricsTrackerFactory = PrometheusMetricsTrackerFactory()
        }),
        databaseConfig = ExposedDatabaseConfig.invoke {
            defaultMaxAttempts = 5
        }
    )

    val user = UserDatabaseUtils(this)
    val member = MemberDatabaseUtils(this)
    val guild = GuildDatabaseUtils(this)

    suspend fun createMissingSchemaAndColumns() {
        newSuspendedTransaction {
            SchemaUtils.createMissingTablesAndColumns(
                BlacklistedUserTable,
                GuildSettingsTable,
                WarnTable
            )
        }
    }

    suspend fun <T> asyncSuspendableTransaction(
        coroutineContext: CoroutineContext? = Dispatchers.IO + SupervisorJob() + CoroutineName("Nabi's Async Database Transaction"),
        db: Database? = null,
        transactionIsolation: Int? = null,
        statement: suspend Transaction.() -> T
    ) = suspendedTransactionAsync(
        coroutineContext,
        db,
        transactionIsolation,
        statement
    )
}