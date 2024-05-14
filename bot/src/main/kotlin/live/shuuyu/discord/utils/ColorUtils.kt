package live.shuuyu.discord.utils

import dev.kord.common.Color

object ColorUtils {
    val ERROR = Color(255, 111, 111)
    val SUCCESS = Color(111, 255, 111)
    val DEFAULT = Color(255, 255, 255)

    // Used in automoderation
    val BAN_COLOR = Color(255, 60, 60)
    val KICK_COLOR = Color(255, 165, 86)
}