package util.types

data class NestedMap<K, V>(val content: Map<K, Either<V, NestedMap<K, V>>>) : Map<K, Either<V, NestedMap<K, V>>> {

	companion object {
		fun <K, V> empty() = NestedMap<K, V>(content = emptyMap())
	}

	override val keys: Set<K> = content.keys
	override val size: Int = content.size

	override fun isEmpty(): Boolean = content.isEmpty()
	override fun containsKey(key: K): Boolean = content.containsKey(key)
	override fun containsValue(value: Either<V, NestedMap<K, V>>): Boolean = content.containsValue(value)

	override fun get(key: K): Either<V, NestedMap<K, V>>? = content[key]

	override val values: Collection<Either<V, NestedMap<K, V>>> = keys.mapNotNull { get(it) }

	override val entries: Set<Map.Entry<K, Either<V, NestedMap<K, V>>>> = keys.mapNotNull {
		when (val value = get(it)) {
			null -> null
			else -> object : Map.Entry<K, Either<V, NestedMap<K, V>>> {
				override val key: K = it
				override val value: Either<V, NestedMap<K, V>> = value
			}
		}
	}.toSet()
}
