package dev.hirth.clog

import com.github.ajalt.mordant.rendering.Theme
import org.slf4j.event.Level

@DslMarker
annotation class ConfigDsl

/**
 * Builder for configuring a [CliLoggerConfig].
 */
@ConfigDsl
class CliLoggerConfigBuilder(private val config: CliLoggerConfig) {
    /**
     * Set the log level.
     */
    var level: Level by config::level

    /**
     * The [Theme] to use for log display, when printing to the terminal.
     */
    var theme: Theme by config::theme

    /**
     * Set the filter function.
     *
     * @see CliLoggerConfig.filter
     */
    fun filter(block: (CliLogger.FilterData) -> Boolean) {
        config.filter = block
    }

    /**
     * Set the message formatting function.
     *
     * @see CliLoggerConfig.format
     */
    fun format(block: (String, Array<out Any?>) -> String) {
        config.format = block
    }

    /**
     * Render the log message.
     *
     * This function is used to display the log message (prefix, timestamp,
     * coloring, etc.) and the throwable's stacktrace.
     */
    fun render(block: (CliLogger.LogMessage) -> String) {
        config.render = block
    }

    /**
     * The actual printing function used to output the log message.
     */
    fun print(block: (String) -> Unit) {
        config.print = block
    }
}