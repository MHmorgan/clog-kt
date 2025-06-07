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
- Only logging to `stdout` or `stderr`
- Colored and formatted logging through [`mordant`](https://github.com/ajalt/mordant)


## Security

This library is not hardened against log-injection, so it is up to the user
to sanitize the log data or to avoid logging user-input.
