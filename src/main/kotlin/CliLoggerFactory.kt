package dev.hirth.clog

import org.slf4j.ILoggerFactory
import java.util.concurrent.ConcurrentHashMap

object CliLoggerFactory : ILoggerFactory {
    private val loggers = ConcurrentHashMap<String, CliLogger>()

    var config: CliLoggerConfig
        private set

    init {
        config = CliLoggerConfig.TERMINAL
    }

    fun level(level: LogLevel) {
        config = config.copy(level = level)
    }

    override fun getLogger(name: String?): CliLogger {
        val caller = name.orEmpty()
        return loggers.getOrPut(caller) {
            CliLogger(caller, config)
        }
    }
}
