package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Unmute
import live.shuuyu.nabi.interactions.commands.moderation.UnmuteExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class UnmuteDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(Unmute.Command.Name, Unmute.Command.Description) {
        defaultMemberPermissions = Permissions {
            + Permission.ModerateMembers
        }

        dmPermission = false

        executor = UnmuteExecutor(nabi)
    }
}