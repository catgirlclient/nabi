package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Mute
import live.shuuyu.nabi.interactions.commands.moderation.MuteExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class MuteDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(Mute.Command.Name, Mute.Command.Description) {
        defaultMemberPermissions = Permissions {
            + Permission.KickMembers
        }

        dmPermission = false

        executor = MuteExecutor(nabi)
    }
}