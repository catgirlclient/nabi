package live.shuuyu.nabi.interactions.commands.discord

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.ChannelInfo
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions

class ChannelInfoExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi) {
    inner class Options: NabiApplicationCommandOptions(language) {
        val channel = optionalChannel(ChannelInfo.Command.ChannelOptionName, ChannelInfo.Command.ChannelOptionDescription)
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val channel = args[options.channel] ?: Channel.from(
            ChannelData.from(rest.channel.getChannel((context as NabiGuildApplicationContext).channelId)),
            kord
        )

        context.sendMessage {
            embed {
                title = channel.data.name.value
                description = context.i18nContext.get(ChannelInfo.Embed.ChannelInfoDescription(
                    channel.id,
                    channel.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate)
                ))
            }
        }
    }
}