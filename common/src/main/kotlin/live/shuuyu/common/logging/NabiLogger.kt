package live.shuuyu.common.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.Marker

public class NabiLogger(name: String) {
    private val logger: Logger = LoggerFactory.getLogger(name)

    public fun error(msg: String): Unit =
        logger.error(msg)

    public fun error(msg: () -> Any?): Unit =
        logger.error(msg.toString())

    public fun error(msg: String, throwable: Throwable? = null): Unit =
        logger.error(msg, throwable)

    public fun error(msg: () -> Any?, throwable: Throwable? = null): Unit =
        logger.error(msg.toString(), throwable)

    public fun warn(msg: String): Unit =
        logger.warn(msg)

    public fun warn(msg: () -> Any?): Unit =
        logger.warn(msg.toString())

    public fun warn(msg: String, throwable: Throwable? = null): Unit =
        logger.warn(msg, throwable)

    public fun warn(msg: () -> Any?, throwable: Throwable? = null): Unit =
        logger.warn(msg.toString(), throwable)

    public fun info(msg: String): Unit =
        logger.info(msg)

    public fun info(msg: () -> Any?): Unit =
        logger.info(msg.toString())

    public fun info(msg: String, throwable: Throwable? = null): Unit =
        logger.info(msg, throwable)

    public fun info(msg: () -> Any?, throwable: Throwable? = null): Unit =
        logger.info(msg.toString(), throwable)

    public fun debug(msg: String): Unit =
        logger.debug(msg)

    public fun debug(msg: () -> Any?): Unit =
        logger.debug(msg.toString())

    public fun debug(msg: String, throwable: Throwable? = null): Unit =
        logger.debug(msg, throwable)

    public fun debug(msg: () -> Any?, throwable: Throwable? = null): Unit =
        logger.debug(msg.toString(), throwable)

    public val isErrorEnabled: Boolean = logger.isErrorEnabled

    public fun isErrorEnabled(marker: Marker): Boolean =
        logger.isErrorEnabled(marker)

    public val isWarnEnabled: Boolean = logger.isWarnEnabled

    public fun isWarnEnabled(marker: Marker): Boolean =
        logger.isWarnEnabled(marker)

    public val isInfoEnabled: Boolean = logger.isInfoEnabled

    public fun isInfoEnabled(marker: Marker): Boolean =
        logger.isInfoEnabled(marker)

    public val isDebugEnabled: Boolean = logger.isDebugEnabled

    public fun isDebugEnabled(marker: Marker): Boolean =
        logger.isDebugEnabled(marker)
}