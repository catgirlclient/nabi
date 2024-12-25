package live.shuuyu.nabi.interactions.commands.moderation.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Slowmode
import live.shuuyu.nabi.i18n.SlowmodeRemove
import live.shuuyu.nabi.interactions.commands.moderation.SlowmodeExecutor
import live.shuuyu.nabi.interactions.commands.moderation.SlowmodeRemoveExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class SlowmodeDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(Slowmode.Command.Name, Slowmode.Command.Description) {
        defaultMemberPermissions = Permissions {
            + Permission.ManageChannels
        }

        dmPermission = false

        executor = SlowmodeExecutor(nabi)

        subcommand(SlowmodeRemove.Command.Name, Slowmode.Command.Description) {
            defaultMemberPermissions = Permissions {
                + Permission.ManageChannels
            }

            dmPermission = false

            executor = SlowmodeRemoveExecutor(nabi)
        }
    }
}