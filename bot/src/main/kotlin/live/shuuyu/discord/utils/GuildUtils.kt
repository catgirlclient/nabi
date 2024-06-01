package live.shuuyu.discord.utils

import dev.kord.core.entity.Asset
import dev.kord.core.entity.Guild
import dev.kord.rest.Image

object GuildUtils {
    fun Guild.getGuildIcon(size: Image.Size): String? {
        val icon = iconHash?.let { Asset.guildIcon(id, it, kord) }

        return icon?.cdnUrl?.toUrl {
            this.format = if(icon.isAnimated) Image.Format.GIF else Image.Format.PNG
            this.size = size
        }
    }

    fun Guild.getGuildBanner(size: Image.Size): String? {
        val banner = bannerHash?.let { Asset.guildBanner(id, it, kord) }

        return banner?.cdnUrl?.toUrl {
            this.format = if(banner.isAnimated) Image.Format.GIF else Image.Format.PNG
            this.size = size
        }
    }
}