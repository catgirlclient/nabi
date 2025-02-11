package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Unban
import live.shuuyu.nabi.interactions.commands.moderation.UnbanExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class UnbanDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(Unban.Command.Name, Unban.Command.Description) {
        defaultMemberPermissions = Permissions {
            + Permission.BanMembers
        }

        dmPermission = false

        executor = UnbanExecutor(nabi)
    }
}