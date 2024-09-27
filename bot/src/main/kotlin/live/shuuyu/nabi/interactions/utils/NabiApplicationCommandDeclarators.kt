package live.shuuyu.nabi.interactions.utils

import dev.kord.common.Locale
import dev.kord.common.entity.Permissions
import live.shuuyu.discordinteraktions.common.commands.*
import live.shuuyu.discordinteraktions.common.commands.MessageCommandDeclaration

class NabiSlashCommandDeclaration(
    override val name: String,
    override val defaultMemberPermissions: Permissions?,
    override val description: String,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val dmPermission: Boolean?,
    override val executor: SlashCommandExecutor?,
    override val nameLocalizations: Map<Locale, String>?,
    override val nsfw: Boolean?,
    override val subcommandGroups: List<SlashCommandGroupDeclaration>,
    override val subcommands: List<SlashCommandDeclaration>
): SlashCommandDeclaration()

class NabiSlashCommandGroupDeclaration(
    override val name: String,
    override val description: String,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val nameLocalizations: Map<Locale, String>?,
    override val nsfw: Boolean?,
    override val subcommands: List<SlashCommandDeclaration>
): SlashCommandGroupDeclaration()

class NabiUserCommandDeclaration(
    override val name: String,
    override val defaultMemberPermissions: Permissions?,
    override val dmPermission: Boolean?,
    override val executor: UserCommandExecutor,
    override val nameLocalizations: Map<Locale, String>?,
    override val nsfw: Boolean?
): UserCommandDeclaration()

class MessageCommandDeclaration(
    override val name: String,
    override val defaultMemberPermissions: Permissions?,
    override val dmPermission: Boolean?,
    override val executor: MessageCommandExecutor,
    override val nameLocalizations: Map<Locale, String>?,
    override val nsfw: Boolean?
): MessageCommandDeclaration()