package live.shuuyu.discord.interactions.commands.slash.moderation

import dev.kord.common.entity.ChannelType
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments

class SlowmodeCommand(nabi: NabiCore): NabiSlashCommandExecutor(nabi) {
    inner class Options: ApplicationCommandOptions() {
        val channel = optionalChannel("channel", "The supplied channel to apply the ratelimit to.") {
            channelTypes = listOf(
                ChannelType.GuildText,
                ChannelType.PrivateThread,
                ChannelType.PublicGuildThread,
                ChannelType.GuildForum
            )
        }
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val channel = args[options.channel] ?: Channel.from(ChannelData.from(rest.channel.getChannel(context.channelId)), kord)


    }

    private fun validate(channel: Channel, executor: User): Map<Channel, List<SlowmodeInteractionResult>> {
        val interaction = mutableListOf<SlowmodeInteractionCheck>()

        when {

        }

        return mapOf()
    }

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