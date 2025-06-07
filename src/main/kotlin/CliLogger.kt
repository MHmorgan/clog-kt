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

    /**
     * Print a success log message.
     *
     * This is an info message which uses the success color from the theme.
     */
    fun success(msg: String, args: Array<out Any?>? = null) {
        config.filter?.let { filter ->
            val data = FilterData(caller, Level.INFO)
            if (!filter(data)) return
        }

        val msg = LogMessage(
            caller = caller,
            level = Level.INFO,
            message = when (args) {
                null -> msg
                else -> config.format(msg, args)
            },
            theme = config.theme,
        )

        val txt = config.render(msg).let {
            config.theme.success(it)
        }
        synchronized(config) {
            config.print(txt)
        }
    }

    /**
     * Print an emphasis log message.
     *
     * This is an info message which uses the info color from the theme.
     */
    fun emphasis(msg: String, args: Array<out Any?>? = null) {
        config.filter?.let { filter ->
            val data = FilterData(caller, Level.INFO)
            if (!filter(data)) return
        }

        val msg = LogMessage(
            caller = caller,
            level = Level.INFO,
            message = when (args) {
                null -> msg
                else -> config.format(msg, args)
            },
            theme = config.theme,
        )

        val txt = config.render(msg).let {
            config.theme.info(it)
        }
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
