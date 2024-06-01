package live.shuuyu.discord.utils

import dev.kord.core.entity.Asset
import dev.kord.core.entity.Member
import dev.kord.rest.Image

object MemberUtils {
    fun Member.getMemberAvatar(size: Image.Size): String? {
        val avatar = memberAvatarHash?.let { Asset.memberAvatar(guildId, id, it, kord) }

        return avatar?.cdnUrl?.toUrl {
            this.format = if(avatar.isAnimated) Image.Format.GIF else Image.Format.PNG
            this.size = size
        }
    }

    fun Member.getMemberBanner(size: Image.Size): String? {
        val icon = bannerHash?.let {
            Asset.memberBanner(guildId, id, it, kord)
        }

        return icon?.cdnUrl?.toUrl {
            this.format = if(icon.isAnimated) Image.Format.GIF else Image.Format.PNG
            this.size = size
        }
    }
}