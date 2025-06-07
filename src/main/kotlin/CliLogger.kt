package dev.hirth.clog

import com.github.ajalt.mordant.rendering.Theme
import org.slf4j.Marker
import org.slf4j.event.Level
import org.slf4j.helpers.AbstractLogger

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
        requireNotNull(messagePattern) { "$caller: Expected non-null message pattern." }

        config.filter?.let { filter ->
            val data = FilterData(caller, level)
            if (!filter(data)) return
        }

        val msg = LogMessage(
            caller = caller,
            level = level,
            message = when (arguments) {
                null -> messagePattern
                else -> config.format(messagePattern, arguments)
            },
            throwable = throwable,
            theme = config.theme,
        )

        val txt = config.render(msg)
        synchronized(config) {
            config.print(txt)
        }
    }

    override fun isTraceEnabled() = config.level == Level.TRACE
    override fun isDebugEnabled() = config.level >= Level.DEBUG
    override fun isInfoEnabled() = config.level >= Level.INFO
    override fun isWarnEnabled() = config.level >= Level.WARN
    override fun isErrorEnabled() = true

    override fun isTraceEnabled(marker: Marker?) = isTraceEnabled()
    override fun isDebugEnabled(marker: Marker?) = isDebugEnabled()
    override fun isInfoEnabled(marker: Marker?) = isInfoEnabled()
    override fun isWarnEnabled(marker: Marker?) = isWarnEnabled()
    override fun isErrorEnabled(marker: Marker?) = isErrorEnabled()

    data class LogMessage(
        val caller: String,
        val level: Level,
        val message: String,
        val throwable: Throwable? = null,
        val theme: Theme,
    )

    data class FilterData(val caller: String, val level: Level)
}
