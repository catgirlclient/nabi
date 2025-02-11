package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Ban
import live.shuuyu.nabi.interactions.commands.moderation.BanExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class BanDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(Ban.Command.Name, Ban.Command.Description) {
        defaultMemberPermissions = Permissions {
            + Permission.BanMembers
        }

        dmPermission = false

        executor = BanExecutor(nabi)
    }
}