package live.shuuyu.nabi.database.utils

import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

open class SnowflakeIdTable : IdTable<Snowflake>() {
    final override val id: Column<EntityID<Snowflake>> = snowflake("id").entityId()

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}