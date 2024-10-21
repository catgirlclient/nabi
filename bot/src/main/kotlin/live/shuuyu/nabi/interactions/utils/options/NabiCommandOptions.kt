package live.shuuyu.nabi.interactions.utils.options

import dev.kord.common.Locale
import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.DiscordAttachment
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import live.shuuyu.discordinteraktions.common.autocomplete.AutocompleteHandler
import live.shuuyu.discordinteraktions.common.commands.options.*
import live.shuuyu.i18n.data.I18nStringData
import live.shuuyu.nabi.utils.i18n.LanguageManager

abstract class NabiCommandOption<T> (
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData
): NameableCommandOption<T> {
    override val name = manager.defaultI18nContext.get(localizedName)
    override val description = manager.defaultI18nContext.get(localizedDescription)
}

class NabiStringCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val required: Boolean,
    override val choices: List<CommandChoice<String>>?,
    override val minLength: Int?,
    override val maxLength: Int?,
    override val autocompleteExecutor: AutocompleteHandler<String>?
): NabiCommandOption<String>(manager, localizedName, localizedDescription), StringCommandOption

class NabiIntegerCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val required: Boolean,
    override val choices: List<CommandChoice<Long>>?,
    override val minValue: Long?,
    override val maxValue: Long?,
    override val autocompleteExecutor: AutocompleteHandler<Long>?
): NabiCommandOption<Long>(manager, localizedName, localizedDescription), IntegerCommandOption

class NabiNumberCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val required: Boolean,
    override val choices: List<CommandChoice<Double>>?,
    override val minValue: Double?,
    override val maxValue: Double?,
    override val autocompleteExecutor: AutocompleteHandler<Double>?
): NabiCommandOption<Double>(manager, localizedName, localizedDescription), NumberCommandOption

class NabiBooleanCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val required: Boolean
): NabiCommandOption<Boolean>(manager, localizedName, localizedDescription), BooleanCommandOption

class NabiUserCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val required: Boolean
): NabiCommandOption<User>(manager, localizedName, localizedDescription), UserCommandOption

class NabiRoleCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val required: Boolean
): NabiCommandOption<Role>(manager, localizedName, localizedDescription), RoleCommandOption

class NabiChannelCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val channelTypes: List<ChannelType>?,
    override val required: Boolean
): NabiCommandOption<Channel>(manager, localizedName, localizedDescription), ChannelCommandOption

class NabiMentionableCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val required: Boolean
): NabiCommandOption<Any>(manager, localizedName, localizedDescription), MentionableCommandOption

class NabiAttachmentCommandOption(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    override val nameLocalizations: Map<Locale, String>?,
    override val descriptionLocalizations: Map<Locale, String>?,
    override val required: Boolean
): NabiCommandOption<DiscordAttachment>(manager, localizedName, localizedDescription), AttachmentCommandOption