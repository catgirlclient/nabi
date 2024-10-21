package live.shuuyu.nabi.interactions.commands.moderation

import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Purge
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions

class PurgeExecutor(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/Purge.toml")) {
    inner class Options: NabiApplicationCommandOptions(language) {
        val messageCount = integer(Purge.Command.CountOptionName, Purge.Command.CountOptionDescription) {
            minValue = 1
        }
        val user = optionalUser(Purge.Command.UserOptionName, Purge.Command.UserOptionDescription)
        val reason = optionalString(Purge.Command.ReasonOptionName, Purge.Command.ReasonOptionDescription) {
            allowedLength = 0..512
        }
    }

    var userList = mutableListOf<User>()

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val channel = Channel.from(ChannelData.from(rest.channel.getChannel(context.channelId)), kord)


    }

    private class PurgeData(
        val executor: User,
        val messageCount: Int,
        val reason: String
    )
}