package live.shuuyu.nabi.utils

fun Long.formatBytes(): String {
    val kilobyte = this / 1024L
    val megabyte = kilobyte / 1024L
    val gigabyte = megabyte / 1024L

    return when {
        kilobyte < 1024L -> "${kilobyte}KB"
        megabyte < 1024L -> "${megabyte}MB"
        else -> "${gigabyte}GB"
    }
}