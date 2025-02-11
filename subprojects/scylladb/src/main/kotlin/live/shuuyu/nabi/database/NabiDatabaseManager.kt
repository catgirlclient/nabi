package live.shuuyu.nabi.database

import com.datastax.oss.driver.api.core.CqlSession

class NabiDatabaseManager(val config: NabiDatabaseConfig) {
    fun initialize() {
        CqlSession.builder().build().use { session ->

        }
    }
}