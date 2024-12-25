package live.shuuyu.nabi.interactions.utils

import live.shuuyu.i18n.data.I18nStringData
import live.shuuyu.nabi.NabiCore

interface NabiApplicationCommandDeclarationWrapper

abstract class NabiSlashCommandDeclarationWrapper(val nabi: NabiCore): NabiApplicationCommandDeclarationWrapper {
    abstract fun declaration(): NabiSlashCommandDeclarationBuilder

    fun slashCommand(
        localizedName: I18nStringData,
        localizedDescription: I18nStringData,
        builder: NabiSlashCommandDeclarationBuilder.() -> (Unit)
    ) = slashCommand(nabi.language, localizedName, localizedDescription, builder)
}

abstract class NabiUserCommandDeclarationWrapper(val nabi: NabiCore): NabiApplicationCommandDeclarationWrapper {
    abstract fun declaration(): NabiUserCommandDeclarationBuilder

    fun userCommand(
        localizedName: I18nStringData,
        executor: NabiUserCommandExecutor,
        builder: NabiUserCommandDeclarationBuilder.() -> (Unit) = {}
    ) = userCommand(nabi.language, localizedName, executor, builder)
}

abstract class NabiMessageCommandDeclarationWrapper(val nabi: NabiCore): NabiApplicationCommandDeclarationWrapper {
    abstract fun declaration(): NabiMessageCommandDeclarationBuilder

    fun messageCommand(
        localizedName: I18nStringData,
        executor: NabiMessageCommandExecutor,
        builder: NabiMessageCommandDeclarationBuilder.() -> (Unit)
    ) = messageCommand(nabi.language, localizedName, executor, builder)
}

