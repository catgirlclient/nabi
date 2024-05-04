package live.shuuyu.discord.interactions.utils

import dev.kord.rest.builder.interaction.ModalBuilder
import dev.kord.rest.builder.message.EmbedBuilder
import net.perfectdreams.discordinteraktions.common.BarebonesInteractionContext
import net.perfectdreams.discordinteraktions.common.builder.message.create.InteractionOrFollowupMessageCreateBuilder
import net.perfectdreams.discordinteraktions.common.modals.ModalExecutorDeclaration

open class NabiBarebonesInteractionContext(open val interaction: BarebonesInteractionContext) {
    suspend inline fun deferChannelMessage() = interaction.deferChannelMessage()

    suspend inline fun deferEphemeralChannelMessage() = interaction.deferChannelMessageEphemerally()

    suspend inline fun respond(builder: InteractionOrFollowupMessageCreateBuilder.() -> (Unit)) =
        interaction.sendMessage { apply(builder) }


    suspend inline fun ephemeralRespond(builder: InteractionOrFollowupMessageCreateBuilder.() -> (Unit)) =
        interaction.sendEphemeralMessage { apply(builder)}

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
}