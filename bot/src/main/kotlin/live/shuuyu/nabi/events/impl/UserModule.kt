package live.shuuyu.nabi.events.impl

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.optional.value
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.gateway.GuildMemberAdd
import dev.kord.gateway.UserUpdate
import io.github.oshai.kotlinlogging.KotlinLogging
import live.shuuyu.common.locale.LanguageManager
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.events.AbstractEventModule
import live.shuuyu.nabi.events.EventContext
import live.shuuyu.nabi.events.EventResult

class UserModule(nabi: NabiCore): AbstractEventModule(nabi) {
    companion object {
        val logger = KotlinLogging.logger {  }
        val i18n = LanguageManager("./locale/events/UserModule.toml")
    }

    override suspend fun onEvent(context: EventContext): EventResult {
        when(val event = context.event) {
            is UserUpdate -> {
                val user = event.user

                if (user.bot.value == true) return EventResult.Return
                matchAvatarHash(user)
            }

            is GuildMemberAdd -> {
                val user = event.member.user.value ?: return EventResult.Continue
                val guildId = event.member.guildId
                val guild = Guild(GuildData.from(rest.guild.getGuild(guildId)), kord)

                val accountAgeConfigId = database.guild.getGuildSettingsConfig(guildId.value.toLong())?.accountAgeConfigId
                val accountAgeConfig = database.guild.getAccountAgeSettingsConfig(accountAgeConfigId) ?: return EventResult.Continue

                val userAccountAge = user.id.timestamp.epochSeconds
                val requiredAccountAge = accountAgeConfig.minAccountAge

                if (requiredAccountAge >= userAccountAge) {
                    guild.kick(
                        user.id,
                        i18n.get("accountAgeReason", mapOf("0" to "[$userAccountAge/$requiredAccountAge]"))
                    )
                }
            }

            else -> {}
        }
        return EventResult.Return
    }

    /**
     * Check if the user's profile picture matches a bot. If they do, kick or ban them.
     */
    private fun matchAvatarHash(user: DiscordUser) {
    }
}