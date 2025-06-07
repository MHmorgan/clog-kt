package dev.hirth.clog

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.event.Level
import org.slf4j.helpers.MessageFormatter
import java.io.PrintStream

class CliLoggerTest {
    lateinit var output: String

    val config = CliLoggerConfig(
        level = Level.INFO,
        format = { msg, args -> MessageFormatter.arrayFormat(msg, args).message },
        render = { "${it.level}: ${it.message}" },
        print = { output = it },
    )

    @BeforeEach
    fun setup() {
        output = ""
    }

    @Test
    fun `Test TRACE level`() {
        val log = CliLogger("test", config.copy(level = Level.TRACE))

        exec { log.trace("trace") }.let {
            assertThat(it).isEqualTo("TRACE: trace")
        }
        exec { log.debug("debug") }.let {
            assertThat(it).isEqualTo("DEBUG: debug")
        }
        exec { log.info("info") }.let {
            assertThat(it).isEqualTo("INFO: info")
        }
        exec { log.warn("warn") }.let {
            assertThat(it).isEqualTo("WARN: warn")
        }
        exec { log.error("error") }.let {
            assertThat(it).isEqualTo("ERROR: error")
        }
    }

    @Test
    fun `Test INFO level`() {
        val log = CliLogger("test", config.copy(level = Level.INFO))

        exec { log.trace("trace") }.let {
            assertThat(it).isEmpty()
        }
        exec { log.debug("debug") }.let {
            assertThat(it).isEmpty()
        }
        exec { log.info("info") }.let {
            assertThat(it).isEqualTo("INFO: info")
        }
        exec { log.warn("warn") }.let {
            assertThat(it).isEqualTo("WARN: warn")
        }
        exec { log.error("error") }.let {
            assertThat(it).isEqualTo("ERROR: error")
        }
    }

    @Test
    fun `Test ERROR level`() {
        val log = CliLogger("test", config.copy(level = Level.ERROR))

        exec { log.trace("trace") }.let {
            assertThat(it).isEmpty()
        }
        exec { log.debug("debug") }.let {
            assertThat(it).isEmpty()
        }
        exec { log.info("info") }.let {
            assertThat(it).isEmpty()
        }
        exec { log.warn("warn") }.let {
            assertThat(it).isEmpty()
        }
        exec { log.error("error") }.let {
            assertThat(it).isEqualTo("ERROR: error")
        }
    }

    @Test
    fun `Test Throwable`() {
        val t = RuntimeException("test-throwable")

        run {
            val log = CliLogger("test", config)
            exec { log.error("error", RuntimeException("test")) }.let {
                assertThat(it).isEqualTo("ERROR: error")
            }
        }

        run {
            val config = config.copy(render = { "${it.level}: ${it.message} ${it.throwable?.message}" })
            val log = CliLogger("test", config)
            exec { log.error("error", t) }.let {
                assertThat(it).isEqualTo("ERROR: error test-throwable")
            }
        }
    }

    fun exec(block: () -> Unit): String {
        block()
        return output.trimIndent()
    }
}