package live.shuuyu.discord.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.commands.moderation.Slowmode
import live.shuuyu.discord.interactions.commands.moderation.SlowmodeRemove
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.slashCommand

class SlowmodeDeclarator(val nabi: NabiCore): SlashCommandDeclarationWrapper {
    companion object {
        val i18n = LanguageManager("./locale/commands/Slowmode.toml")
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        defaultMemberPermissions = Permissions {
            + Permission.ManageChannels
        }

        dmPermission = false

        executor = Slowmode(nabi)

        subcommand(i18n.get("removeName"), i18n.get("removeDescription")) {
            defaultMemberPermissions = Permissions {
                + Permission.ManageChannels
            }

            dmPermission = false

            executor = SlowmodeRemove(nabi)
        }
    }
}