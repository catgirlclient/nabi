package live.shuuyu.nabi.interactions.utils.options

import dev.kord.common.entity.DiscordAttachment
import dev.kord.core.entity.Role
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import live.shuuyu.discordinteraktions.common.commands.options.*
import live.shuuyu.i18n.data.I18nStringData
import live.shuuyu.nabi.utils.i18n.LanguageManager

abstract class NabiStringCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): StringCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): StringCommandOption = NabiStringCommandOption(
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        required,
        choices?.map { it.build() },
        minLength,
        maxLength,
        autocompleteExecutor
    )
}

class NabiStringCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiStringCommandOptionBuilderBase<String>(manager, localizedName, localizedDescription, true)

class NabiNullableStringCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiStringCommandOptionBuilderBase<String?>(manager, localizedName, localizedDescription, false)

abstract class NabiIntegerCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): IntegerCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): IntegerCommandOption = NabiIntegerCommandOption(
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        required,
        choices?.map { it.build() },
        minValue,
        maxValue,
        autocompleteExecutor
    )
}

class NabiIntegerCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiIntegerCommandOptionBuilderBase<Long>(manager, localizedName, localizedDescription, true)

class NabiNullableIntegerCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiIntegerCommandOptionBuilderBase<Long?>(manager, localizedName, localizedDescription, false)

abstract class NabiNumberCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): NumberCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): NumberCommandOption = NabiNumberCommandOption(
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        required,
        choices?.map { it.build() },
        minValue,
        maxValue,
        autocompleteExecutor
    )
}

class NabiNumberCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiNumberCommandOptionBuilderBase<Number>(manager, localizedName, localizedDescription, true)

class NabiNullableNumberCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiNumberCommandOptionBuilderBase<Number?>(manager, localizedName, localizedDescription, false)

abstract class NabiBooleanCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): BooleanCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): BooleanCommandOption = NabiBooleanCommandOption(
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        required
    )
}

class NabiBooleanCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiBooleanCommandOptionBuilderBase<Boolean>(manager, localizedName, localizedDescription, true)

class NabiNullableBooleanCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiBooleanCommandOptionBuilderBase<Boolean?>(manager, localizedName, localizedDescription, true)

abstract class NabiUserCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): UserCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): UserCommandOption = NabiUserCommandOption (
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        required
    )
}

class NabiUserCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiUserCommandOptionBuilderBase<User>(manager, localizedName, localizedDescription, true)

class NabiNullableUserCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiUserCommandOptionBuilderBase<User?>(manager, localizedName, localizedDescription, false)

abstract class NabiRoleCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): RoleCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): RoleCommandOption = NabiRoleCommandOption(
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        required
    )
}

class NabiRoleCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiRoleCommandOptionBuilderBase<Role>(manager, localizedName, localizedDescription, true)

class NabiNullableRoleCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiRoleCommandOptionBuilderBase<Role?>(manager, localizedName, localizedDescription, false)

abstract class NabiChannelCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): ChannelCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): ChannelCommandOption = NabiChannelCommandOption(
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        channelTypes,
        required
    )
}

class NabiChannelCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiChannelCommandOptionBuilderBase<Channel>(manager, localizedName, localizedDescription, true)

class NabiNullableChannelCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiChannelCommandOptionBuilderBase<Channel?>(manager, localizedName, localizedDescription, false)

abstract class NabiMentionableCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): MentionableCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): MentionableCommandOption = NabiMentionableCommandOption(
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        required
    )
}

class NabiMentionableCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiMentionableCommandOptionBuilderBase<Any>(manager, localizedName, localizedDescription, true)

class NabiNullableMentionableCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiMentionableCommandOptionBuilderBase<Any?>(manager, localizedName, localizedDescription, false)

abstract class NabiAttachmentCommandOptionBuilderBase<T>(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData,
    override val required: Boolean
): AttachmentCommandOptionBuilderBase<T>() {
    override val name: String
        get() = TODO("Not yet implemented")
    override val description: String
        get() = TODO("Not yet implemented")

    override fun build(): AttachmentCommandOption = NabiAttachmentCommandOption(
        manager,
        localizedName,
        localizedDescription,
        nameLocalizations,
        descriptionLocalizations,
        required
    )
}

class NabiAttachmentCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiAttachmentCommandOptionBuilderBase<DiscordAttachment>(manager, localizedName, localizedDescription, true)

class NabiNullableAttachmentCommandOptionBuilder(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData
): NabiAttachmentCommandOptionBuilderBase<DiscordAttachment?>(manager, localizedName, localizedDescription, false)