package io.github.gr3gdev.fenrir;

import io.github.gr3gdev.fenrir.logger.Level;

import java.text.MessageFormat;

/**
 * Fenrir Logger.
 */
public class Logger {

    private final java.util.logging.Logger internalLogger;

    /**
     * Construct a new Logger.
     *
     * @param name the logger name.
     */
    public Logger(String name) {
        this.internalLogger = java.util.logging.Logger.getLogger(name);
    }

    /**
     * Log a message with {@link Level#INFO}.
     *
     * @param message the string message format in java.text.MessageFormat format.
     * @param args    an optional list of parameters to the message (may be none).
     */
    public void info(String message, Object... args) {
        this.internalLogger.log(Level.INFO, new MessageFormat(message).format(args));
    }

    /**
     * Log a message with {@link Level#DEBUG}.
     *
     * @param message the string message format in java.text.MessageFormat format.
     * @param args    an optional list of parameters to the message (may be none).
     */
    public void debug(String message, Object... args) {
        this.internalLogger.log(Level.DEBUG, new MessageFormat(message).format(args));
    }

    /**
     * Log a message with {@link Level#WARN}.
     *
     * @param message the string message format in java.text.MessageFormat format.
     * @param args    an optional list of parameters to the message (may be none).
     */
    public void warn(String message, Object... args) {
        this.internalLogger.log(Level.WARN, new MessageFormat(message).format(args));
    }

    /**
     * Log a message with {@link Level#WARN}.
     *
     * @param message the string message.
     * @param thrown  Throwable associated with log message.
     */
    public void warn(String message, Throwable thrown) {
        this.internalLogger.log(Level.WARN, message, thrown);
    }

    /**
     * Log a message with {@link Level#ERROR}.
     *
     * @param message the string message format in java.text.MessageFormat format.
     * @param args    an optional list of parameters to the message (may be none).
     */
    public void error(String message, Object... args) {
        this.internalLogger.log(Level.ERROR, new MessageFormat(message).format(args));
    }

    /**
     * Log a message with {@link Level#ERROR}.
     *
     * @param message the string message.
     * @param thrown  Throwable associated with log message.
     */
    public void error(String message, Throwable thrown) {
        this.internalLogger.log(Level.ERROR, message, thrown);
    }
}
