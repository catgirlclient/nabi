package live.shuuyu.discord.interactions.commands.slash.general

import dev.kord.common.DiscordTimestampStyle
import dev.kord.common.toMessageFormat
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.builder.message.embed
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.commands.slashCommand

class ChannelInfo(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/ChannelInfo.toml")), SlashCommandDeclarationWrapper {
    inner class Options: ApplicationCommandOptions() {
        val channel = optionalChannel(i18n.get("channelOptionName"), i18n.get("channelOptionDescription"))
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val channel = args[options.channel] ?: Channel.from(
            ChannelData.from(rest.channel.getChannel((context as NabiGuildApplicationContext).channelId)),
            kord
        )

        context.respond {
            embed {
                title = channel.data.name.value
                description = i18n.get("embedDescription", mapOf(
                    "0" to channel.id,
                    "1" to channel.id.timestamp.toMessageFormat(DiscordTimestampStyle.LongDate)
                ))
            }
        }
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        executor = this@ChannelInfo
    }
}