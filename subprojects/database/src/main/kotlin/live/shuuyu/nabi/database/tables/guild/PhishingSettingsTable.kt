package live.shuuyu.nabi.database.tables.guild

import live.shuuyu.nabi.database.utils.PunishmentType
import org.jetbrains.exposed.dao.id.LongIdTable
import kotlin.time.Duration.Companion.days

object PhishingSettingsTable: LongIdTable() {
    val enabled = bool("enabled").default(false)
    val punishmentType = enumerationByName("punishment_type", 4, PunishmentType::class).default(PunishmentType.Kick)
    val defaultMuteDuration = long("default_mute_duration").default(1.days.inWholeDays)

    /**
     * Channel where suspicious links will not trigger any events related to phishing.
     */
    val whitelistedChannels = array<Long>("whitelisted_channels").default(emptyList())
}