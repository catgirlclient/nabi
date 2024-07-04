package live.shuuyu.discord.webserver

import live.shuuyu.discord.NabiCore
import live.shuuyu.discord.utils.config.DiscordConfig
import net.perfectdreams.discordinteraktions.webserver.InteractionsServer

class NabiWebserverManager(nabi: NabiCore, config: DiscordConfig) {

    val interactionServer = InteractionsServer(nabi.interaktions, config.publicKey, config.port)

    fun initialize() {
        interactionServer.start()
    }
}