package live.shuuyu.discord.interactions.commands.slash

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.rest.route.Route
import kotlinx.datetime.Clock
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.formatBytes
import net.perfectdreams.discordinteraktions.common.builder.message.actionRow
import net.perfectdreams.discordinteraktions.common.builder.message.embed
import net.perfectdreams.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import net.perfectdreams.discordinteraktions.common.commands.options.SlashCommandArguments
import net.perfectdreams.discordinteraktions.common.commands.slashCommand
import java.lang.management.ManagementFactory

class NabiInfoCommand(nabi: NabiCore): NabiSlashCommandExecutor(nabi), SlashCommandDeclarationWrapper {
    @OptIn(KordUnsafe::class, KordExperimental::class)
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val gatewayInfo = nabi.rest.unsafe(Route.GatewayBotGet) {}
        val os = ManagementFactory.getOperatingSystemMXBean()

        // memory stuff
        val runtime = Runtime.getRuntime()
        val memoryUsage = runtime.freeMemory().formatBytes()
        val totalMemoryUsage = runtime.totalMemory().formatBytes()
        val memoryAllocated = runtime.maxMemory().formatBytes()

        context.respond {
            embed {
                title = "Nabi"
                field {
                    name = "» System Information"
                    value = buildString {
                        append("**Operating System:** ${os.name} (${os.arch}; ${os.version})")
                        append("\n")
                        append("**RAM Usage:** $memoryUsage/$totalMemoryUsage [$memoryAllocated]")
                        append("\n")
                        append("**Java Version:** ${System.getProperty("java.version")}")
                        append("\n")
                        append("**Java Distributor:** ${System.getProperty("java.vendor")}")
                        append("\n")
                        append("**Kotlin Version:** ${KotlinVersion.CURRENT}")
                    }
                }
                field {
                    name = "» Sharding Information"
                    value = buildString {
                        append("**Gateway Ping:** ${kord.gateway.averagePing?.inWholeMilliseconds} ms")
                        append("\n")
                        append("**Shards Launched This Session:** ${gatewayInfo.shards}")
                        append("\n")
                        append("**Session Limit:** ${gatewayInfo.sessionStartLimit.remaining}/${gatewayInfo.sessionStartLimit.total}")
                    }
                }
                image = kord.getSelf().defaultAvatar.cdnUrl.toUrl()
                timestamp = Clock.System.now()
            }
        }
    }

    override fun declaration() = slashCommand("info", "Sends useless information about Nabi.") {
        executor = this@NabiInfoCommand
    }
}