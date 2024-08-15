package live.shuuyu.nabi.interactions.utils

import live.shuuyu.discordinteraktions.common.builder.message.create.InteractionOrFollowupMessageCreateBuilder

open class CommandException: RuntimeException()

open class PublicCommandException(val builder: InteractionOrFollowupMessageCreateBuilder): CommandException()

open class EphemeralCommandException(val builder: InteractionOrFollowupMessageCreateBuilder): CommandException()