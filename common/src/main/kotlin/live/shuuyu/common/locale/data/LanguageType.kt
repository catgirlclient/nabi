package live.shuuyu.common.locale.data

@Deprecated(
    "Avoid using the legacy i18n system",
    level = DeprecationLevel.WARNING
)
public data class LanguageType(
    public val string: Map<String, String>
)