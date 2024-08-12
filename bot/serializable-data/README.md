# Serializable Data
Since Nabi uses [Kord's RestOnly](https://github.com/kordlib/kord/blob/2cdf33cb4cbb07c8c37b861777339e429fe27c59/core/src/commonMain/kotlin/Kord.kt#L404)
builder, Kord's cache is disabled. Due to this, we rely on [DragonflyDB](https://www.dragonflydb.io/) and [Caffeine](https://github.com/ben-manes/caffeine)
for our own in-house cache, and store it within these data classes to be then upserted into our cache.

