package live.shuuyu.nabi.database.utils

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.currentDialect

fun Table.snowflake(name: String) = registerColumn(name, SnowflakeColumnType())

class SnowflakeColumnType: ColumnType<Snowflake>() {
    override fun sqlType(): String {
        return currentDialect.dataTypeProvider.longType()
    }

    override fun valueFromDB(value: Any): Snowflake {
        return when (value) {
            is String -> Snowflake(value.toString())
            is Long -> Snowflake(value)
            is ULong -> Snowflake(value)
            is Number -> Snowflake(value.toLong())
            else -> throw Exception("Value ($value) cannot be parsed into a Snowflake value.")
        }
    }

    override fun notNullValueToDB(value: Snowflake): Any {
        return value.value.toLong()
    }
}
