package live.shuuyu.nabi.interactions.utils

import dev.kord.common.Locale
import dev.kord.common.entity.Permissions
import live.shuuyu.discordinteraktions.common.commands.*
import live.shuuyu.i18n.data.I18nStringData
import live.shuuyu.nabi.utils.i18n.LanguageManager

class NabiSlashCommandDeclaration(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    override val nameLocalizations: Map<Locale, String>? = null,
    val localizedDescription: I18nStringData,
    override val descriptionLocalizations: Map<Locale, String>? = null,
    override val executor: SlashCommandExecutor? = null,
    override val defaultMemberPermissions: Permissions?,
    override val dmPermission: Boolean?,
    override val subcommands: List<SlashCommandDeclaration>,
    override val subcommandGroups: List<SlashCommandGroupDeclaration>,
    override val nsfw: Boolean?
): SlashCommandDeclaration() {
    override val name: String = manager.defaultI18nContext.get(localizedName)
    override val description: String = manager.defaultI18nContext.get(localizedDescription)
}

class NabiSlashCommandGroupDeclaration(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    val localizedDescription: I18nStringData,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val subcommands: List<SlashCommandDeclaration>,
    override val nsfw: Boolean?
): SlashCommandGroupDeclaration() {
    override val name: String = manager.defaultI18nContext.get(localizedName)
    override val description: String = manager.defaultI18nContext.get(localizedDescription)
}

class NabiUserCommandDeclaration(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val executor: UserCommandExecutor,
    override val defaultMemberPermissions: Permissions?,
    override val dmPermission: Boolean?,
    override val nsfw: Boolean?
): UserCommandDeclaration() {
    override val name: String = manager.defaultI18nContext.get(localizedName)
}

class NabiMessageCommandDeclaration(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    override val nameLocalizations: Map<Locale, String>? = null,
    override val executor: MessageCommandExecutor,
    override val defaultMemberPermissions: Permissions?,
    override val dmPermission: Boolean?,
    override val nsfw: Boolean?
): MessageCommandDeclaration() {
    override val name: String = manager.defaultI18nContext.get(localizedName)
}