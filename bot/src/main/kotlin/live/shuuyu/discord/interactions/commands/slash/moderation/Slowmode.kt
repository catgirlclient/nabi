package live.shuuyu.discord.interactions.commands.slash.moderation

import dev.kord.common.entity.ChannelType
import dev.kord.core.behavior.channel.edit
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.builder.message.embed
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import kotlin.time.Duration

class Slowmode(nabi: NabiCore): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Slowmode.toml")) {
    inner class Options: ApplicationCommandOptions() {
        val channel = optionalChannel("channel", "The supplied channel to apply the ratelimit to.") {
            channelTypes = listOf(
                ChannelType.GuildText,
                ChannelType.PrivateThread,
                ChannelType.PublicGuildThread,
                ChannelType.GuildForum
            )
        }

        val duration = string(i18n.get("durationOptionName"), i18n.get("durationOptionDescription"))
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val channel = args[options.channel] ?: Channel.from(ChannelData.from(rest.channel.getChannel(context.channelId)), kord)
    }

    private suspend fun slowmode(data: SlowmodeData) {
        val channel = data.channel
        val duration = data.duration

        when(channel) {
            is TextChannel -> channel.edit {
                rateLimitPerUser = duration
            }
        }
    }

    private fun validate(channel: Channel, executor: User): Map<Channel, List<SlowmodeInteractionResult>> {
        val interaction = mutableListOf<SlowmodeInteractionCheck>()

        when {

        }

        return mapOf()
    }

    private fun createSlowmodeConfirmationEmbed(channel: Channel, duration: Duration): UserMessageCreateBuilder.() -> (Unit) = {
        embed {
            title = i18n.get("confirmationEmbedTitle")
            description = i18n.get("confirmationEmbedDescription", mapOf("0" to "${duration.absoluteValue}"))
            color = ColorUtils.SUCCESS
            timestamp = Clock.System.now()
        }
    }

    private data class SlowmodeData(
        val channel: Channel,
        val duration: Duration,
        val executor: User,
        val reason: String
    )

    private data class SlowmodeInteractionCheck(
        val result: SlowmodeInteractionResult,
        val channel: Channel,
        val executor: User
    )

    private enum class SlowmodeInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        INVALID_CHANNEL,
        SUCCESS
    }
}