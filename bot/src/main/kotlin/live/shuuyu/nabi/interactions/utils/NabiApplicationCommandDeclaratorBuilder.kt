package live.shuuyu.nabi.interactions.utils

import dev.kord.common.Locale
import dev.kord.common.entity.Permissions
import live.shuuyu.discordinteraktions.common.annotations.InteraKTionsDsl
import live.shuuyu.discordinteraktions.common.commands.MessageCommandDeclaration
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclaration
import live.shuuyu.discordinteraktions.common.commands.SlashCommandGroupDeclaration
import live.shuuyu.discordinteraktions.common.commands.UserCommandDeclaration
import live.shuuyu.i18n.data.I18nStringData
import live.shuuyu.nabi.utils.i18n.LanguageManager

fun slashCommand(
    manager: LanguageManager,
    localizedName: I18nStringData,
    localizedDescription: I18nStringData,
    builder: NabiSlashCommandDeclarationBuilder.() -> (Unit)
): NabiSlashCommandDeclarationBuilder = NabiSlashCommandDeclarationBuilder(
    manager,
    localizedName,
    localizedDescription
).apply(builder)

@InteraKTionsDsl
class NabiSlashCommandDeclarationBuilder(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData
) {
    var nameLocalizations: Map<Locale, String>? = null
    var descriptionLocalizations: Map<Locale, String>? = null
    var executor: NabiSlashCommandExecutor? = null
    val subcommands: MutableList<NabiSlashCommandDeclarationBuilder> = mutableListOf()
    val subcommandGroups: MutableList<NabiSlashCommandGroupDeclarationBuilder> = mutableListOf()
    // Only root commands can have permissions and dmPermission
    var defaultMemberPermissions: Permissions? = null
    var dmPermission: Boolean? = null
    var nsfw: Boolean? = null

    fun subcommand(
        localizedName: I18nStringData,
        localizedDescription: I18nStringData,
        block: NabiSlashCommandDeclarationBuilder.() -> (Unit)
    ) {
        subcommands += NabiSlashCommandDeclarationBuilder(manager, localizedName, localizedDescription).apply(block)
    }

    fun subcommandGroup(
        localizedName: I18nStringData,
        localizedDescription: I18nStringData,
        block: NabiSlashCommandGroupDeclarationBuilder.() -> (Unit)
    ) {
        subcommandGroups += NabiSlashCommandGroupDeclarationBuilder(manager, localizedName, localizedDescription).apply(block)
    }

    fun build(): SlashCommandDeclaration {
        return NabiSlashCommandDeclaration(
            manager,
            localizedName,
            nameLocalizations,
            localizedDescription,
            descriptionLocalizations,
            executor,
            defaultMemberPermissions,
            dmPermission,
            subcommands.map { it.build() },
            subcommandGroups.map { it.build() },
            nsfw
        )
    }
}

@InteraKTionsDsl
class NabiSlashCommandGroupDeclarationBuilder(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val localizedDescription: I18nStringData
) {
    var nameLocalizations: Map<Locale, String>? = null
    var descriptionLocalizations: Map<Locale, String>? = null
    // Groups can't have executors!
    val subcommands: MutableList<NabiSlashCommandDeclarationBuilder> = mutableListOf()
    var nsfw: Boolean? = null

    fun subcommand(
        localizedName: I18nStringData,
        localizedDescription: I18nStringData,
        block: NabiSlashCommandDeclarationBuilder.() -> (Unit)
    ) {
        subcommands += NabiSlashCommandDeclarationBuilder(manager, localizedName, localizedDescription).apply(block)
    }

    fun build(): SlashCommandGroupDeclaration {
        return NabiSlashCommandGroupDeclaration(
            manager,
            localizedName,
            nameLocalizations,
            localizedDescription,
            descriptionLocalizations,
            subcommands.map { it.build() },
            nsfw
        )
    }
}

fun userCommand(
    manager: LanguageManager,
    localizedName: I18nStringData,
    executor: NabiUserCommandExecutor,
    builder: NabiUserCommandDeclarationBuilder.() -> (Unit)
): NabiUserCommandDeclarationBuilder = NabiUserCommandDeclarationBuilder(
    manager,
    localizedName,
    executor
).apply(builder)

@InteraKTionsDsl
class NabiUserCommandDeclarationBuilder(
    val manager: LanguageManager,
    val localizedName: I18nStringData,
    val executor: NabiUserCommandExecutor
) {
    var nameLocalizations: Map<Locale, String>? = null
    var defaultMemberPermissions: Permissions? = null
    var dmPermission: Boolean? = null
    var nsfw: Boolean? = null

    fun build(): UserCommandDeclaration {
        return NabiUserCommandDeclaration(
            manager,
            localizedName,
            nameLocalizations,
            executor,
            defaultMemberPermissions,
            dmPermission,
            nsfw
        )
    }
}

fun messageCommand(
    manager: LanguageManager,
    localizedName: I18nStringData,
    executor: NabiMessageCommandExecutor,
    builder: NabiMessageCommandDeclarationBuilder.() -> (Unit)
): NabiMessageCommandDeclarationBuilder = NabiMessageCommandDeclarationBuilder(
    manager,
    localizedName,
    executor
).apply(builder)

@InteraKTionsDsl
class NabiMessageCommandDeclarationBuilder(
    val languageManager: LanguageManager,
    val localizedName: I18nStringData,
    val executor: NabiMessageCommandExecutor
) {
    var nameLocalizations: Map<Locale, String>? = null
    var defaultMemberPermissions: Permissions? = null
    var dmPermission: Boolean? = null
    var nsfw: Boolean? = null

    fun build(): MessageCommandDeclaration {
        return NabiMessageCommandDeclaration(
            languageManager,
            localizedName,
            nameLocalizations,
            executor,
            defaultMemberPermissions,
            dmPermission,
            nsfw
        )
    }
}