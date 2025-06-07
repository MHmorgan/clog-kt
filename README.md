Clog 🪠
=======

CLI logging library for Kotlin and SLF4J.

### Goals

- Simplicity
- Programmatic configuration
- CLI-oriented functionality
- Kotlin-idiomatic

### Features

- Designed for [Slf4j](https://www.slf4j.org/)
- Logging to any [`PrintStream`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/io/PrintStream.html)
- Colored and formatted logging through [`mordant`](https://github.com/ajalt/mordant)
- Only prints colors to TTY output

## Security

This library is not hardened against log-injection, so it is up to the user
to sanitize the log data or to avoid logging user-input.
