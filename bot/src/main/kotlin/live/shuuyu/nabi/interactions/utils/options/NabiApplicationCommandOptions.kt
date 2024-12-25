package live.shuuyu.nabi.interactions.utils.options

import live.shuuyu.discordinteraktions.common.commands.options.ApplicationCommandOptions
import live.shuuyu.discordinteraktions.common.commands.options.register
import live.shuuyu.i18n.data.I18nStringData
import live.shuuyu.nabi.utils.i18n.LanguageManager

abstract class NabiApplicationCommandOptions(private val manager: LanguageManager): ApplicationCommandOptions() {
    fun string(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiStringCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiStringCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalString(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableStringCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableStringCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun integer(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiIntegerCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiIntegerCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalInteger(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableIntegerCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableIntegerCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun number(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNumberCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNumberCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalNumber(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableNumberCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableNumberCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun boolean(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiBooleanCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiBooleanCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalBoolean(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableBooleanCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableBooleanCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun user(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiUserCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiUserCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalUser(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableUserCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableUserCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun role(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiRoleCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiRoleCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalRole(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableRoleCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableRoleCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun channel(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiChannelCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiChannelCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalChannel(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableChannelCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableChannelCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun mentionable(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiMentionableCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiMentionableCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalMentionable(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableMentionableCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableMentionableCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun attachment(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiAttachmentCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiAttachmentCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }

    fun optionalAttachment(
        name: I18nStringData,
        description: I18nStringData,
        builder: NabiNullableAttachmentCommandOptionBuilder.() -> (Unit) = {}
    ) = NabiNullableAttachmentCommandOptionBuilder(manager, name, description)
        .apply(builder)
        .let {
            register(it)
        }
}