package dev.hirth.clog

import java.io.PrintStream

data class CliLoggerConfig(
    val level: LogLevel,
    val out: PrintStream,
    val format: (CliLogger.LogMessage) -> String,
) {

    companion object {
        @JvmStatic
        val TERMINAL = CliLoggerConfig(
            level = LogLevel.INFO,
            out = System.err,
            format = { "${it.level}: ${it.message}" },
        )
    }
}
