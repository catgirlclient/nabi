package live.shuuyu.discord.interactions.commands.slash.moderation

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.ban
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.Channel
import dev.kord.rest.request.KtorRequestException
import io.ktor.util.cio.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.database.tables.LoggingChannel
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiGuildApplicationContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.options.ApplicationCommandOptions
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import okhttp3.internal.wait
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

class Ban(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/language/BanCommand.toml")), SlashCommandDeclarationWrapper {
    inner class Options: ApplicationCommandOptions() {
        val user = user(
            i18n.get("userOptionName"),
            i18n.get("userOptionDescription")
        )

        val reason = optionalString(
            i18n.get("reasonOptionName"),
            i18n.get("reasonOptionDescription")
        )

        val deleteMessageDuration = optionalString(
            i18n.get("deleteMessageDurationOptionName"),
            i18n.get("deleteMessageDurationOptionDescription")
        )
    }

    override val options = Options()

    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        if (context !is NabiGuildApplicationContext)
            return

        val target = args[options.user]
        val executor = context.sender
        val guildData = GuildData.from(rest.guild.getGuild(context.guildId))
        val reason = args[options.reason]
        val deleteMessageDuration = Duration.parse(args[options.deleteMessageDuration] ?: "7d")

        val data = BanData(target, executor, guildData, reason, deleteMessageDuration)

        val interactonCheck = validate(data)
        val failInteractionCheck = interactonCheck.filter { it.result != BanInteractionResult.SUCCESS }
        val successInteractionCheck = interactonCheck - failInteractionCheck.toSet()

        if (successInteractionCheck.isEmpty()) {
            context.fail {
                for (fail in failInteractionCheck) {

                }
            }
        }
    }

    private suspend fun ban(data: BanData) {
        val target = data.target
        val executor = data.executor
        val guild = Guild(data.guild, kord)

        try {
            guild.ban(target.id) {
                reason = data.reason
                deleteMessageDuration = data.deleteMessageDuration
            }
        } catch (e: KtorRequestException) {
            
        }
    }

    private suspend fun validate(data: BanData): List<BanInteractionCheck> {
        val check = mutableListOf<BanInteractionCheck>()

        val target = data.target
        val executor = data.executor
        val guild = Guild(data.guild, kord)
        val deleteMessageDuration = data.deleteMessageDuration

        val targetAsMember = target.asMemberOrNull(guild.id) ?: target as? Member
        val executorAsMember = target.asMember(guild.id)


        val checkIfBanned = guild.bans.filter { it.user.id == target.id }


        when {
            deleteMessageDuration !in 0.seconds..7.days -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.DELETE_MESSAGE_DURATION_OUTSIDE_OF_RANGE
                )
            )

             checkIfBanned.equals(target.id)-> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.TARGET_ALREADY_BANNED
                )
            )

            target.id == guild.ownerId -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.TARGET_IS_OWNER
                )
            )

            target == executor -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.TARGET_IS_SELF
                )
            )

            else -> check.add(
                BanInteractionCheck(
                    target,
                    executor,
                    BanInteractionResult.SUCCESS
                )
            )
        }

        return check
    }

    private data class BanData(
        val target: User,
        val executor: User,
        val guild: GuildData,
        val reason: String?,
        val deleteMessageDuration: Duration
    ) {
        init {
            require(deleteMessageDuration in 0.seconds..7.days) {
                "This should NEVER return! Did something break?"
            }
        }
    }

    private class BanInteractionCheck(
        val target: User,
        val executor: User,
        val result: BanInteractionResult
    )

    private enum class BanInteractionResult {
        INSUFFICIENT_PERMISSIONS,
        TARGET_PERMISSION_IS_EQUAL_OR_HIGHER,
        DELETE_MESSAGE_DURATION_OUTSIDE_OF_RANGE,
        TARGET_ALREADY_BANNED,
        TARGET_IS_OWNER,
        TARGET_IS_SELF,
        SUCCESS
    }

    override fun declaration() = slashCommand(i18n.get("name"), i18n.get("description")) {


        executor = this@Ban
    }
}