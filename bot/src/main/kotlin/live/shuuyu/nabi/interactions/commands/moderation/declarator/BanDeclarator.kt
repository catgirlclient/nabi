package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.interactions.commands.moderation.BanExecutor

class BanDeclarator(val nabi: NabiCore): SlashCommandDeclarationWrapper {
    companion object {
        val i18n = LanguageManager("./locale/commands/Ban.toml")
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {
        defaultMemberPermissions = Permissions {
            + Permission.BanMembers
        }

        dmPermission = false

        executor = BanExecutor(nabi)
    }
}