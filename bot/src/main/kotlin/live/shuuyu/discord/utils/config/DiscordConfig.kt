package live.shuuyu.discord.utils.config

import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

/**
 * @param token The token of the bot
 * @param applicationId The applicationId of the bot
 * @param defaultGuildId The default guild of the bot.
 * @param shards The amount of shards created for the bot.
 * @param defaultPrefix The default prefix of all chat commands. This can be changed.
 * @param ownerIds The owners associated with Nabi
 * @param publicKey The public key associated with the bot. This is for HTTP interactions.
 * @param port The port of the webserver. This is for HTTP interactions.
 */
@Serializable
data class DiscordConfig(
    val token: String,
    val applicationId: Snowflake,
    val defaultGuildId: Snowflake,
    val shards: Int,
    val defaultPrefix: String,
    val ownerIds: List<Snowflake>,
    val publicKey: String,
    val port: Int,
)