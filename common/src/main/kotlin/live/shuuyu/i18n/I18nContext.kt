package live.shuuyu.i18n

import com.ibm.icu.text.MessageFormat
import live.shuuyu.i18n.data.I18nListData
import live.shuuyu.i18n.data.I18nStringData

public class I18nContext(private val data: I18nData) {
    /**
     * Gets the content associated with the key, and replaces the nodes inside the content with
     * the arguments supplied in the array.
     *
     * @param key The key associated with the content
     * @param arguments Replaces the nodes in the content with the supplied arguments.
     *
     * @throws Exception If the key cannot be identified.
     *
     * @since 0.0.1
     */
    public fun get(key: String, vararg arguments: Any): String {
        try {
            val content = data.text.strings[key] ?: error("Key $key does not contain any content!")
            return MessageFormat.format(content, *arguments)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return key
    }

    /**
     * Gets the content associated with the key, and replaces the nodes with the supplied arguments
     * in the given map.
     *
     * @param key The key associated with the content.
     * @param arguments Replaces the nodes in the content specified by the map with the supplied argument.
     *
     * @throws Exception If the key cannot be identified.
     *
     * @since 0.0.1
     */
    public fun get(key: String, arguments: Map<String, Any?>): String = get(key, arguments)

    /**
     * Gets the content associated with the key, and replaces the nodes with the supplied arguments
     * in the given map in the given [I18nStringData].
     *
     * @param data See [I18nStringData].
     *
     * @throws Exception If the key cannot be identified.
     *
     * @see [I18nStringData]
     *
     * @since 0.0.1
     */
    public fun get(data: I18nStringData): String = get(data.key, data.arguments)

    /**
     * Gets the content associated with the key.
     *
     * @param key The key associated with the content.
     *
     * @throws Exception If the key cannot be identified.
     *
     * @since 0.0.1
     */
    // This needs to be an empty map or array because this should only fetch the content.
    public fun get(key: String): String = get(key, emptyMap())

    /**
     * Gets the list of contents associated with the key, and replaces the nodes inside the content with
     * the arguments supplied in the array.
     *
     * @param key The key associated with the list of content/strings.
     * @param arguments Replaces the nodes in the content with the supplied arguments in the array.
     *
     * @throws Exception If the key cannot be identified.
     *
     * @since 0.0.1
     */
    public fun getList(key: String, vararg arguments: Map<String, Any?>): List<String> {
        try {
            val contents = data.text.lists[key] ?: error("Key $key does not contain any list of content!")
            return contents.map { MessageFormat.format(it, arguments) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOf()
    }

    /**
     * Gets the list of content associated with the key, and replaces the nodes inside the content with
     * the arguments supplied by the map. **Note: The node name should match that of the given key in the
     * map!**
     *
     * @param key The key associated with the list of content.
     * @param arguments Replaces the nodes in the content specified by the map with the supplied argument.
     *
     * @throws Exception If the key cannot be identified.
     *
     * @since 0.0.1
     */
    public fun getList(key: String, arguments: Map<String, Any?>): List<String> = getList(key, arguments)

    /**
     * Gets the list of content associated with the key, and replaces the nodes inside the content with
     * the arguments supplied by the map.
     *
     * @param data See [I18nListData].
     *
     * @see [I18nListData]
     *
     * @since 0.0.1
     */
    public fun getList(data: I18nListData): List<String> = getList(data.key, data.arguments)

    /**
     * Gets the list of content associated with the key.
     *
     * @param key The key associated with the list of content.
     *
     * @throws Exception If the key cannot be identified.
     *
     * @since 0.0.1
     */
    public fun getList(key: String): List<String> = getList(key, emptyMap())
}