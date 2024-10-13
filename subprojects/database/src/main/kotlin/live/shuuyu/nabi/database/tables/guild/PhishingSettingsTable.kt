package live.shuuyu.nabi.database.tables.guild

import live.shuuyu.nabi.database.utils.PunishmentType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import kotlin.time.Duration.Companion.days

public object PhishingSettingsTable: LongIdTable() {
    public val enabled: Column<Boolean> = bool("enabled").default(false)
    public val punishmentType: Column<PunishmentType> = enumerationByName("punishment_type", 4, PunishmentType::class).default(PunishmentType.Kick)
    public val defaultMuteDuration: Column<Long> = long("default_mute_duration").default(1.days.inWholeDays)

    /**
     * Channel where suspicious links will not trigger any events related to phishing.
     */
    public val whitelistedChannels: Column<List<Long>> = array<Long>("whitelisted_channels").default(emptyList())
}