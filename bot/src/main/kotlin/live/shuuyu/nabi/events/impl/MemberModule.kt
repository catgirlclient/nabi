package live.shuuyu.nabi.events.impl

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.User
import dev.kord.gateway.GuildMemberAdd
import dev.kord.gateway.GuildMemberRemove
import dev.kord.rest.builder.message.create.UserMessageCreateBuilder
import dev.kord.rest.request.RestRequestException
import live.shuuyu.nabi.NabiCore
import live.shuuyu.nabi.events.AbstractEventModule
import live.shuuyu.nabi.events.EventContext
import live.shuuyu.nabi.events.EventResult

class MemberModule(nabi: NabiCore): AbstractEventModule(nabi) {
    override suspend fun onEvent(context: EventContext): EventResult {
        when(val event = context.event) {
            is GuildMemberAdd -> {
                val user = event.member.user.value ?: return EventResult.Continue
                val guildId = event.member.guildId

                return sendGuildMessage(user, guildId, JoinType.Join)
            }

            is GuildMemberRemove -> {
                val user = event.member.user
                val guildId = event.member.guildId

                return sendGuildMessage(user, guildId, JoinType.Leave)
            }

            else -> {}
        }

        return EventResult.Return
    }

    private suspend fun sendGuildMessage(
        user: DiscordUser,
        guildId: Snowflake,
        type: JoinType
    ): EventResult {
        val user = User(user.toData(), kord)
        val guild = Guild(GuildData.from(rest.guild.getGuild(guildId)), kord)

        val getGuildConfig = database.guild.getGuildConfig(guildId.value.toLong()) ?: return EventResult.Continue

        try {
            when(type) {
                JoinType.Join -> {
                    val welcomeConfigId = getGuildConfig.leaveConfigId ?: return EventResult.Continue
                    val welcomeChannelId = database.guild.getWelcomeChannelConfig(welcomeConfigId)?.channelId ?: return EventResult.Continue

                    rest.channel.createMessage(Snowflake(welcomeChannelId), createGuildMessage(user, type))
                }
                JoinType.Leave -> {
                    val leaveConfigId = getGuildConfig.leaveConfigId ?: return EventResult.Continue
                    val leaveChannelId = database.guild.getLeaveChannelConfig(leaveConfigId)?.channelId ?: return EventResult.Continue

                    rest.channel.createMessage(Snowflake(leaveChannelId), createGuildMessage(user, type))
                }
            }
        } catch (e: RestRequestException) {

        }

        return EventResult.Continue
    }

    private suspend fun createGuildMessage(target: User, type: JoinType): UserMessageCreateBuilder.() -> (Unit) = {

    }

    enum class JoinType{
        Join, Leave
    }
}