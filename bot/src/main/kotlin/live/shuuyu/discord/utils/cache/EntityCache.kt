package live.shuuyu.discord.utils.cache

import dev.kord.rest.service.RestClient

/**
 * Fetches the cached value of an entity. If the value is null, the value
 * will be upsert and sent.
 */
class EntityCache(val rest: RestClient)