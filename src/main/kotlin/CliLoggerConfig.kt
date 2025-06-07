package dev.hirth.clog

import com.github.ajalt.mordant.rendering.TextStyles.bold
import com.github.ajalt.mordant.rendering.Theme
import com.github.ajalt.mordant.terminal.Terminal
import org.slf4j.event.Level
import org.slf4j.helpers.MessageFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val t = Terminal()

data class CliLoggerConfig(
    /**
     * The log level.
     */
    var level: Level = Level.INFO,

    /**
     * A filter function that is called for each log message.
     * If the function returns `false` the log message will be ignored.
     *
     * Can be used for more fine-grained filtering than the [level] property.
     * This allows filtering based on the caller.
     */
    var filter: ((CliLogger.FilterData) -> Boolean)? = null,

    /**
     * The message formatting function.
     *
     * This function is used to convert the `messagePattern` and `arguments`
     * into a string, which later is passed to the [render] function.
     * This function is what you want to overwrite if you want to sanitize
     * the log parameters to prevent log injection attacks.
     *
     * By default, the [org.slf4j.helpers.MessageFormatter] is used.
     *
     * If you want to change the log message display (prefix, timestamp,
     * coloring, etc.) you must use the [render] function.
     */
    var format: (String, Array<out Any?>) -> String,

    /**
     * Render the log message.
     *
     * This function is used to display the log message (prefix, timestamp,
     * coloring, etc.) and the throwable's stacktrace.
     */
    var render: (CliLogger.LogMessage) -> String,

    /**
     * The actual printing function used to output the log message.
     */
    var print: (String) -> Unit,

    /**
     * The [Theme] to use for log display, when printing to the terminal.
     */
    var theme: Theme = Theme.Default,
) {
    companion object {
        @JvmStatic
        val TERMINAL = CliLoggerConfig(
            format = { msg, args -> MessageFormatter.arrayFormat(msg, args).message },
            render = ::terminalRender,
            print = { t.println(it, stderr = true)},
        )

        @JvmStatic
        val NON_TERMINAL = CliLoggerConfig(
            format = { msg, args -> MessageFormatter.arrayFormat(msg, args).message },
            render = ::fileRender,
            print = { t.println(it, stderr = false) },
        )
    }
}

/**
 * The render function used by the [CliLoggerConfig.TERMINAL] configuration.
 */
fun terminalRender(msg: CliLogger.LogMessage): String {
    val prefix = when (msg.level) {
        Level.TRACE -> "[.] "
        Level.DEBUG -> "[ ] "
        Level.INFO -> "[*] "
        Level.WARN -> "[!] "
        Level.ERROR -> "[!!]"
    }.let {
        bold(it)
    }

    val style = when (msg.level) {
        Level.TRACE, Level.DEBUG -> msg.theme.muted::invoke
        Level.INFO -> {{ it }}
        Level.WARN -> msg.theme.warning::invoke
        Level.ERROR -> msg.theme.danger::invoke
    }

    return buildString {
        append(style("$prefix ${msg.message}"))
        msg.throwable?.let {
            appendLine()
            append(it.stackTraceToString())
        }
    }
}

/**
 * The render function used by the [CliLoggerConfig.NON_TERMINAL] configuration.
 *
 * This function will not colorize the output.
 */
fun fileRender(msg: CliLogger.LogMessage): String {
    val time = LocalDateTime.now()
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    val lvl = msg.level.name.padEnd(5)
    return buildString {
        append("[$lvl|$time] ${msg.caller}  ${msg.message}")
        msg.throwable?.let {
            appendLine()
            append(it.stackTraceToString())
        }
    }
}
