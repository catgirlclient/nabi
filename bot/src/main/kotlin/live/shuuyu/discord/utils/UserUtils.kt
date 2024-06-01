package live.shuuyu.discord.utils

import dev.kord.core.entity.Asset
import dev.kord.core.entity.User
import dev.kord.rest.Image

object UserUtils {
    fun User.getUserAvatar(size: Image.Size): String {
        val avatar = avatarHash?.let { Asset.userAvatar(id, it, kord) } ?: Asset.Companion.defaultUserAvatar(id, kord)

        return avatar.cdnUrl.toUrl {
            this.format = if(avatar.isAnimated) Image.Format.GIF else Image.Format.PNG
            this.size = size
        }
    }

    fun User.getUserBanner(size: Image.Size): String? {
        val banner = bannerHash?.let { Asset.userBanner(id, it, kord) }

        return banner?.cdnUrl?.toUrl {
            this.format = if(banner.isAnimated) Image.Format.GIF else Image.Format.PNG
            this.size = size
        }
    }
}