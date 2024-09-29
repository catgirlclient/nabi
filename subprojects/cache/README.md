# cache
Nabi, by using [Kord's ``restOnly`` function](https://github.com/kordlib/kord/blob/main/core/src/commonMain/kotlin/builder/kord/RestOnlyBuilder.kt), 
doesn't have a built-in caching module, and will thus rely solely on Discord's REST API for retrieving information. This
means that it'll constantly send requests to and from the REST API for each request, possibly ratelimiting us in many cases.
Due to this, we exclusively rely on Redis/Dragonfly and Caffeine to do all of our caching. 

# Dependencies used
* [Caffeine](https://github.com/ben-manes/caffeine)
* [Redisson](https://github.com/redisson/redisson)
* [DragonflyDB](https://github.com/dragonflydb/dragonfly)