package live.shuuyu.discord.interactions.utils

import net.perfectdreams.discordinteraktions.common.builder.message.create.InteractionOrFollowupMessageCreateBuilder

open class CommandException: RuntimeException()

open class PublicCommandException(val builder: InteractionOrFollowupMessageCreateBuilder): CommandException()

open class EphemeralCommandException(val builder: InteractionOrFollowupMessageCreateBuilder): CommandException()