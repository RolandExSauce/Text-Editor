package com.solideditor.programm.Logging;
import org.slf4j.Logger;


public class LoggingUtils {
    private static final Logger logger = ApplicationLogs.getRootLogger(); // root logger for info
    private static final Logger errLogger = ApplicationLogs.getErrorLogger(); // error logger for errors

    public enum LogLevel {
        INFO,
        ERROR
    };

    /**
     * Logs a message with the provided details at the specified log level.
     *
     * @param logLevel           The log level (INFO or ERROR).
     * @param className          The class name where the log is generated.
     * @param errorOrSuccessCode The error or success code associated with the log.
     */
    public static void log(LogLevel logLevel, String className,
                           ErrorOrSuccessCode errorOrSuccessCode) {
        //format message
        String msg = String.format("%s: %s", "MESSAGE CODE", errorOrSuccessCode.getCode());
        // Log based on the log level
        switch (logLevel) {
            case INFO -> logger.info(LoggingMsg.create(className, msg));
            case ERROR -> errLogger.error(LoggingMsg.create(className, msg));
            default -> throw new IllegalArgumentException("Unsupported log level: " + logLevel);
        };
    };
};