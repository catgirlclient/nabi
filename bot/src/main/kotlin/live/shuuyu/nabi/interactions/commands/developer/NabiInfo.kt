package live.shuuyu.nabi.interactions.commands.developer

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.rest.route.Route
import kotlinx.datetime.Clock
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.discordinteraktions.common.builder.message.actionRow
import live.shuuyu.discordinteraktions.common.builder.message.embed
import live.shuuyu.discordinteraktions.common.commands.SlashCommandDeclarationWrapper
import live.shuuyu.discordinteraktions.common.commands.options.SlashCommandArguments
import live.shuuyu.discordinteraktions.common.commands.slashCommand
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.i18n.NabiInfo
import live.shuuyu.nabi.interactions.utils.NabiApplicationCommandContext
import live.shuuyu.nabi.interactions.utils.NabiSlashCommandExecutor
import live.shuuyu.nabi.utils.ColorUtils
import live.shuuyu.nabi.utils.formatBytes
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
                    name = context.i18nContext.get(NabiInfo.Embed.SystemFieldName)
                    value = context.i18nContext.get(NabiInfo.Embed.SystemFieldDescription(
                        os.name,
                        os.arch,
                        os.version,
                        runtime.freeMemory().formatBytes(),
                        runtime.totalMemory().formatBytes(),
                        runtime.maxMemory().formatBytes(),
                        System.getProperty("java.version"),
                        System.getProperty("java.vendor"),
                        KotlinVersion.CURRENT
                    ))
                }

                field {
                    name = context.i18nContext.get(NabiInfo.Embed.GatewayFieldName)
                    value = context.i18nContext.get(NabiInfo.Embed.GatewayFieldDescription(
                        kord.gateway.averagePing?.inWholeMilliseconds!!,
                        gatewayInfo.shards,
                        gatewayInfo.sessionStartLimit.remaining,
                        gatewayInfo.sessionStartLimit.total
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