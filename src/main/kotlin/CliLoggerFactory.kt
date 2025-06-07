package dev.hirth.clog

import com.github.ajalt.mordant.terminal.Terminal
import org.slf4j.ILoggerFactory
import java.util.concurrent.ConcurrentHashMap

object CliLoggerFactory : ILoggerFactory {
    private val loggers = ConcurrentHashMap<String, CliLogger>()

    /**
     * The configuration used for all loggers.
     */
    val config: CliLoggerConfig

    init {
        val ti = Terminal().terminalInfo
        config = when (ti.outputInteractive) {
            true -> CliLoggerConfig.TERMINAL
            else -> CliLoggerConfig.FILE
        }
    }

    fun config(block: CliLoggerConfigBuilder.() -> Unit) {
        CliLoggerConfigBuilder(config).block()
    }

    override fun getLogger(name: String?): CliLogger {
        val caller = name.orEmpty()
        return loggers.getOrPut(caller) {
            CliLogger(caller, config)
        }
    }
}
