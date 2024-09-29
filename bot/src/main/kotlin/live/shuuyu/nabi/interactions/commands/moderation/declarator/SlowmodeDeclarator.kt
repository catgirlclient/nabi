package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.moderation.SlowmodeExecutor
import live.shuuyu.nabi.interactions.commands.moderation.SlowmodeRemoveExecutor

class SlowmodeDeclarator(val nabi: NabiCore): SlashCommandDeclarationWrapper {
    companion object {
        val i18n = LanguageManager("./locale/commands/Slowmode.toml")
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        defaultMemberPermissions = Permissions {
            + Permission.ManageChannels
        }

        dmPermission = false

        executor = SlowmodeExecutor(nabi)

        subcommand(i18n.get("removeName"), i18n.get("removeDescription")) {
            defaultMemberPermissions = Permissions {
                + Permission.ManageChannels
            }

            dmPermission = false

            executor = SlowmodeRemoveExecutor(nabi)
        }
    }
}