package live.shuuyu.common.caching.impl

interface EntityCache<Key: Any, Value: Any> {

    suspend fun get(key: Key): Value?

    suspend fun remove()

    suspend fun filter(predicate: ((Value) -> Boolean)): Sequence<Value>

    suspend fun set(key: Key, value: Value)

    suspend fun firstOrNull(predicate: (Value) -> Boolean): Value?

    suspend fun clear()
}