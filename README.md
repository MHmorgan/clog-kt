Clog 🪠
=======

> 🚀 **A modern CLI logging library for Kotlin that brings color and clarity to your terminal**

Clog is a powerful, easy-to-use logging library designed specifically for command-line applications. Built on top of SLF4J, it provides beautiful terminal output with colors, customizable formatting, and TTY detection.

## 📋 Overview

Clog automatically detects your output environment and switches between:
- 🎨 **Terminal mode**: Colorized output with visual prefixes for interactive sessions
- 📄 **File mode**: Clean, timestamped logs perfect for file output and CI/CD

## 🎯 Goals

- 🎯 **Simplicity** - Zero-configuration startup with sensible defaults
- ⚙️ **Programmatic configuration** - Full Kotlin DSL for customization
- 🖥️ **CLI-oriented functionality** - Built for command-line applications
- 🦾 **Kotlin-idiomatic** - Leverages Kotlin's language features

## ✨ Features

- 🔧 **Drop-in SLF4J replacement** - Works with existing [SLF4J](https://www.slf4j.org/) code
- 🎨 **Colored terminal output** - Beautiful formatting through [`mordant`](https://github.com/ajalt/mordant)
- 🖨️ **Flexible output** - Log to any [`PrintStream`](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/io/PrintStream.html)
- 🤖 **Smart TTY detection** - Only prints colors to interactive terminals
- 🎭 **Custom themes** - Fully customizable color schemes
- 🔍 **Advanced filtering** - Caller-based and level-based filtering
- 📝 **Custom formatting** - Override message formatting and rendering

## 🚀 Quick Start

### Standard SLF4J Usage

```kotlin
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("MyApp")
logger.info("Application started")
logger.warn("This is a warning")
logger.error("Something went wrong")
```

### Enhanced CLI Methods

```kotlin
import dev.hirth.clog.CliLoggerFactory

val logger = CliLoggerFactory.getLogger("MyApp")
logger.success("✅ Operation completed successfully!")
logger.emphasis("📢 Important information")
```

## ⚙️ Configuration

### DSL Configuration

```kotlin
import dev.hirth.clog.CliLoggerFactory
import org.slf4j.event.Level

CliLoggerFactory.config {
    level = Level.DEBUG
    
    // Filter by caller name
    filter { data -> 
        data.caller.startsWith("com.myapp") 
    }
    
    // Custom message formatting
    format { message, args ->
        "🔍 $message".format(*args)
    }
    
    // Custom rendering
    render { logMessage ->
        "[${logMessage.level}] ${logMessage.caller}: ${logMessage.message}"
    }
}
```

### Pre-built Configurations

```kotlin
// Terminal mode (default for interactive sessions)
val terminalConfig = CliLoggerConfig.TERMINAL

// File mode (default for non-interactive output)
val fileConfig = CliLoggerConfig.FILE
```

## 🎨 Output Examples

### 🖥️ Terminal Mode
```
[*] Application starting...
[✅] Database connection established
[!] Cache miss for key: user:123
[!!] Failed to process request
```

### 📄 File Mode
```
[INFO |2023-12-07 14:30:15] MyApp  Application starting...
[INFO |2023-12-07 14:30:16] MyApp  Database connection established  
[WARN |2023-12-07 14:30:17] MyApp  Cache miss for key: user:123
[ERROR|2023-12-07 14:30:18] MyApp  Failed to process request
```

## 🎭 Advanced Features

### Custom Themes

```kotlin
import com.github.ajalt.mordant.rendering.Theme

CliLoggerFactory.config {
    theme = Theme.Default.copy(
        info = { text -> blue(text) },
        warning = { text -> yellow(text) },
        danger = { text -> red(text) },
        success = { text -> green(text) }
    )
}
```

### Log Level Control

```kotlin
import org.slf4j.event.Level

CliLoggerFactory.config {
    level = Level.DEBUG  // Show debug and above
    
    // Dynamic filtering
    filter { data ->
        when (data.level) {
            Level.ERROR -> true  // Always show errors
            Level.DEBUG -> data.caller.contains("debug")
            else -> true
        }
    }
}
```

### Exception Handling

```kotlin
try {
    riskyOperation()
} catch (e: Exception) {
    logger.error("Operation failed", e)  // Includes full stack trace
}
```

### Custom Output Destinations

```kotlin
import java.io.FileOutputStream
import java.io.PrintStream

CliLoggerFactory.config {
    print { message ->
        // Log to both console and file
        System.err.println(message)
        FileOutputStream("app.log", true).use { file ->
            PrintStream(file).println(message)
        }
    }
}
```

## 📦 Installation

Add to your `build.gradle.kts`:

```kotlin
dependencies {
    implementation("dev.hirth:clog-kt:VERSION")
}
```

## 🔒 Security

⚠️ **Important**: This library is not hardened against log injection attacks. Always sanitize user input before logging:

```kotlin
// ❌ Dangerous - don't log user input directly
logger.info("User input: $userInput")

// ✅ Safe - sanitize first
val sanitized = userInput.replace('\n', ' ').replace('\r', ' ')
logger.info("User input: $sanitized")

// ✅ Better - use custom formatter
CliLoggerFactory.config {
    format { message, args ->
        val sanitizedArgs = args.map { arg ->
            arg?.toString()?.replace(Regex("[\r\n\t]"), " ") ?: "null"
        }.toTypedArray()
        message.format(*sanitizedArgs)
    }
}
```
