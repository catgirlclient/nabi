package live.shuuyu.nabi.interactions.commands.developer.declarator

import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.Blacklist
import live.shuuyu.nabi.i18n.BlacklistRemove
import live.shuuyu.nabi.interactions.commands.developer.blacklist.BlacklistExecutor
import live.shuuyu.nabi.interactions.commands.developer.blacklist.BlacklistRemoveExecutor
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandDeclarationWrapper

class BlacklistDeclarator(nabi: NabiCore): NabiSlashCommandDeclarationWrapper(nabi) {
    override fun declaration() = slashCommand(Blacklist.Command.Name, Blacklist.Command.Description) {
        // No one should be able to see this besides owners in default guild (Which are developers).
        defaultMemberPermissions = Permissions {
            + Permission.Administrator
        }

        dmPermission = false

        executor = BlacklistExecutor(nabi)

        subcommand(BlacklistRemove.Command.Name, BlacklistRemove.Command.Description) {
            defaultMemberPermissions = Permissions {
                + Permission.Administrator
            }

            dmPermission = false

            executor = BlacklistRemoveExecutor(nabi)
        }
    }
}