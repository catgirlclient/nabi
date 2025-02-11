package live.shuuyu.nabi.interactions.commands.discord.user

import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.UserInfo
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.interactions.utils.options.NabiApplicationCommandOptions

class UserInfoSlashExecutor(nabi: NabiCore): NabiSlashCommandExecutor(nabi), UserInteractionHandler {
    inner class Options: NabiApplicationCommandOptions(language) {
        val user = optionalUser(UserInfo.Command.UserOptionName, UserInfo.Command.UserOptionDescription)
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val user = args[options.user] ?: context.sender
        val guild = Guild(GuildData.from(rest.guild.getGuild((context as NabiGuildApplicationContext).guildId)), kord)

        context.sendMessage {
            createUserInfoMessage(context.i18nContext, user, guild)
        }
    }
}