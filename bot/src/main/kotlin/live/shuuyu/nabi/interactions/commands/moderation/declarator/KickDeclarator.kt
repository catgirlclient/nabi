package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Kick
import live.shuuyu.nabi.interactions.commands.moderation.KickExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class KickDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(Kick.Command.Name, Kick.Command.Description) {
        defaultMemberPermissions = Permissions {
            + Permission.KickMembers
        }

        dmPermission = false

        executor = KickExecutor(nabi)
    }

}