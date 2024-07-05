package live.shuuyu.discord.interactions.utils

import dev.kord.rest.builder.interaction.ModalBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import live.shuuyu.discordinteraktions.common.BarebonesInteractionContext
import live.shuuyu.discordinteraktions.common.builder.message.allowedMentions
import live.shuuyu.discordinteraktions.common.builder.message.create.InteractionOrFollowupMessageCreateBuilder
import live.shuuyu.discordinteraktions.common.modals.ModalExecutorDeclaration

open class NabiBarebonesInteractionContext(open val interaction: BarebonesInteractionContext) {
    suspend inline fun deferChannelMessage() = interaction.deferChannelMessage()

    suspend inline fun deferEphemeralChannelMessage() = interaction.deferChannelMessageEphemerally()

    suspend inline fun respond(builder: InteractionOrFollowupMessageCreateBuilder.() -> (Unit)) =
        interaction.sendMessage {
            allowedMentions {
                repliedUser = true
            }

            apply(builder)
        }


    suspend inline fun ephemeralRespond(builder: InteractionOrFollowupMessageCreateBuilder.() -> (Unit)) =
        interaction.sendEphemeralMessage {
            allowedMentions {
                repliedUser = true
            }

            apply(builder)
        }

    suspend inline fun embed(builder: EmbedBuilder.() -> (Unit)) {
        respond {
            embeds = (embeds ?: mutableListOf()).also {
                it.add(EmbedBuilder().apply(builder))
            }
        }
    }

    suspend inline fun ephemeralEmbed(builder: EmbedBuilder.() -> (Unit)) {
        ephemeralRespond {
            embeds = (embeds ?: mutableListOf()).also {
                it.add(EmbedBuilder().apply(builder))
            }
        }
    }

    suspend fun modal(declaration: ModalExecutorDeclaration, title: String, builder: ModalBuilder.() -> (Unit)) =
        interaction.sendModal(declaration.id, title, builder)

    suspend fun modal(declaration: ModalExecutorDeclaration, data: String, title: String, builder: ModalBuilder.() -> (Unit)) =
        interaction.sendModal(declaration, data, title, builder)

    suspend fun modal(id: String, data: String, title: String, builder: ModalBuilder.() -> (Unit)) =
        interaction.sendModal(id, data, title, builder)

    suspend fun modal(idWithData: String, title: String, builder: ModalBuilder.() -> (Unit)) =
        interaction.sendModal(idWithData, title, builder)

    inline fun fail(builder: InteractionOrFollowupMessageCreateBuilder.() -> (Unit)): Nothing =
        throw PublicCommandException(InteractionOrFollowupMessageCreateBuilder(false).apply(builder))

    inline fun ephemeralFail(builder: InteractionOrFollowupMessageCreateBuilder.() -> (Unit)): Nothing =
        throw EphemeralCommandException(InteractionOrFollowupMessageCreateBuilder(true).apply(builder))
}