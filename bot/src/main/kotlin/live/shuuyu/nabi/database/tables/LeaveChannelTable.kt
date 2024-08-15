package live.shuuyu.nabi.database.tables

import org.jetbrains.exposed.dao.id.LongIdTable

object LeaveChannelTable: LongIdTable() {
    /**
     * Sets whether the module should send messages to the provided channel.
     */
    val enabled = bool("enabled").default(false)

    /**
     * The id of the channel where the messages are sent to. If left null,
     * the module will not send anything.
     */
    val channelId = long("channel_id").nullable()

    /**
     * The custom message associated.
     */
    val customMessage = text("custom_message").nullable()

    /**
     * Sets whether the module should fail silently and not send an error message.
     * This should almost never be disabled by the user unless they're a psychopath.
     */
    val silentFail = bool("silent_fail").default(true)
}