package util.types

sealed interface NestedMap<K, V> {
	companion object {
		fun <K, V> empty() = NestedMapNode<K, V>(content = emptyMap())
	}
}

data class NestedMapLeaf<K, V>(val content: V) : NestedMap<K, V>
data class NestedMapNode<K, V>(val content: Map<K, NestedMap<K, V>>) : NestedMap<K, V>, Map<K, V> {
	override val keys: Set<K> = content.keys
	override val size: Int = content.size

	override fun isEmpty(): Boolean = content.isEmpty()
	override fun containsKey(key: K): Boolean = content.containsKey(key)
	override fun containsValue(value: V): Boolean = content.containsValue(NestedMapLeaf(value))

	override fun get(key: K): V? = when (val value = content[key]) {
		is NestedMapLeaf -> value.content
		is NestedMapNode -> null
		null -> null
	}

	override val values: Collection<V> = keys.mapNotNull { get(it) }

	override val entries: Set<Map.Entry<K, V>> = keys.mapNotNull {
		when (val value = get(it)) {
			null -> null
			else ->
				object : Map.Entry<K, V> {
					override val key: K = it
					override val value: V = value
				}
		}
	}.toSet()
}
