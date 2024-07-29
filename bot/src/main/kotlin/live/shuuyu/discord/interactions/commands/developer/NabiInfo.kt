package live.shuuyu.discord.interactions.commands.developer

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.rest.route.Route
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.discord.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.discord.utils.ColorUtils
import live.shuuyu.discord.utils.formatBytes
import live.shuuyu.discordinteraktions.common.builder.message.actionRow
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import java.lang.management.ManagementFactory

class NabiInfo(
    nabi: NabiCore
): NabiSlashCommandExecutor(nabi, LanguageManager("./locale/commands/NabiInfo.toml")), SlashCommandDeclarationWrapper {
    @OptIn(KordUnsafe::class, KordExperimental::class)
    override suspend fun execute(context: NabiApplicationCommandContext, args: SlashCommandArguments) {
        val gatewayInfo = nabi.rest.unsafe(Route.GatewayBotGet) {}
        val os = ManagementFactory.getOperatingSystemMXBean()
        val runtime = Runtime.getRuntime()

        context.sendMessage {
            embed {
                title = "Nabi"
                field {
                    name = "» System Information"
                    value = i18n.get("embedBodySystemInformation", mapOf(
                        "0" to os.name,
                        "1" to os.arch,
                        "2" to os.version,
                        "3" to runtime.freeMemory().formatBytes(),
                        "4" to runtime.totalMemory().formatBytes(),
                        "5" to runtime.maxMemory().formatBytes(),
                        "6" to System.getProperty("java.version"),
                        "7" to System.getProperty("java.vendor"),
                        "8" to KotlinVersion.CURRENT
                    ))
                }

                field {
                    name = "» Sharding Information"
                    value = i18n.get("embedBodyGatewayInformation", mapOf(
                        "0" to kord.gateway.averagePing?.inWholeMilliseconds,
                        "1" to gatewayInfo.shards,
                        "2" to gatewayInfo.sessionStartLimit.remaining,
                        "3" to gatewayInfo.sessionStartLimit.total
                    ))
                }

                thumbnail {
                    url = kord.getSelf().avatar?.cdnUrl?.toUrl() ?: kord.getSelf().defaultAvatar.cdnUrl.toUrl()
                }

                color = ColorUtils.DEFAULT
                timestamp = Clock.System.now()
            }
            actionRow {
                linkButton("https://github.com/catgirlclient/discord/tree/main") {
                     label = "Source Code"
                }
                linkButton("https://discord.gg/42da8JWwKa") {
                    label = "Support Server"
                }
            }
        }
    }

    override fun declaration() = slashCommand("info", "Sends useless information about Nabi.") {
        executor = this@NabiInfo
    }
}