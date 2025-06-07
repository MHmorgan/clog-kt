package dev.hirth.clog

import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.helpers.AbstractLogger
import org.slf4j.helpers.MessageFormatter

/**
 * The log levels supported by the [CliLogger].
 *
 * The order of the levels is the same as the order of the levels used by SLF4J.
 */
enum class LogLevel { TRACE, DEBUG, INFO, WARN, ERROR }

class CliLogger internal constructor(
    private val caller: String,
    private val config: CliLoggerConfig,
) : AbstractLogger() {
    override fun getFullyQualifiedCallerName() = caller

    override fun handleNormalizedLoggingCall(
        level: Level?,
        marker: Marker?,
        messagePattern: String?,
        arguments: Array<out Any?>?,
        throwable: Throwable?
    ) {
        requireNotNull(level) { "$caller: Expected non-null log level." }

        val fmt = when (throwable) {
            null -> MessageFormatter.arrayFormat(messagePattern, arguments)
            else -> MessageFormatter.arrayFormat(messagePattern, arguments, throwable)
        }

        val msg = LogMessage(
            level = level,
            message = fmt.message,
            throwable = throwable,
        )

        val txt = config.format(msg)
        config.out.println(txt)
    }

    override fun isTraceEnabled() = config.level == LogLevel.TRACE
    override fun isDebugEnabled() = config.level <= LogLevel.DEBUG
    override fun isInfoEnabled() = config.level <= LogLevel.INFO
    override fun isWarnEnabled() = config.level <= LogLevel.WARN
    override fun isErrorEnabled() = config.level <= LogLevel.ERROR

    override fun isTraceEnabled(marker: Marker?) = isTraceEnabled()
    override fun isDebugEnabled(marker: Marker?) = isDebugEnabled()
    override fun isInfoEnabled(marker: Marker?) = isInfoEnabled()
    override fun isWarnEnabled(marker: Marker?) = isWarnEnabled()
    override fun isErrorEnabled(marker: Marker?) = isErrorEnabled()

    data class LogMessage(
        val level: Level,
        val message: String,
        val throwable: Throwable? = null,
    )
}
