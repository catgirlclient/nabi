package live.shuuyu.nabi.interactions.commands.developer.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.developer.blacklist.Blacklist
import live.shuuyu.nabi.interactions.commands.developer.blacklist.BlacklistRemove

class BlacklistDeclarator(val nabi: NabiCore): SlashCommandDeclarationWrapper {
    val i18n = LanguageManager("./locale/commands/Blacklist.toml")

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        // No one should be able to see this besides owners in default guild (Which are developers).
        defaultMemberPermissions = Permissions {
            + Permission.Administrator
        }

        dmPermission = false

        executor = Blacklist(nabi)

        subcommand(i18n.get("removeName"), i18n.get("removeDescription")) {
            defaultMemberPermissions = Permissions {
                + Permission.Administrator
            }

            dmPermission = false

            executor = BlacklistRemove(nabi)
        }
    }
}