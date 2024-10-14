package live.shuuyu.nabi.database.tables.guild

import live.shuuyu.nabi.database.utils.PunishmentType
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.Column
import kotlin.time.Duration.Companion.days

public object PhishingSettingsTable: LongIdTable() {
    public val enabled: Column<Boolean> = bool("enabled").default(false)
    public val channelId: Column<Long?> = long("channel_Id").nullable()

    /**
     * This will send it to the channel of which the message it has been sent, not the logging channel.
     */
    public val sendMessageToChannel: Column<Boolean> = bool("send_message_to_user")
    public val punishmentType: Column<PunishmentType> = enumerationByName("punishment_type", 5, PunishmentType::class).default(PunishmentType.Kick)
    public val defaultMuteDuration: Column<Long> = long("default_mute_duration").default(1.days.inWholeDays)

    /**
     * Channel where suspicious links will not trigger any events related to phishing.
     */
    public val whitelistedChannels: Column<List<Long>> = array<Long>("whitelisted_channels").default(emptyList())
}