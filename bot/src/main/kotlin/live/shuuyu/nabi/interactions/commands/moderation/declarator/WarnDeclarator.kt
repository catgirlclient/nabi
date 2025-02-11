package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Warn
import live.shuuyu.nabi.interactions.commands.moderation.WarnExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class WarnDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(Warn.Command.Name, Warn.Command.Description) {
        defaultMemberPermissions = Permissions {
            + Permission.ModerateMembers
        }

        dmPermission = false

        executor = WarnExecutor(nabi)
    }
}