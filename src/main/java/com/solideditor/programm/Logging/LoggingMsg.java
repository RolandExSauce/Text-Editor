package com.solideditor.programm.Logging;


// Log message with the corresponding class
public class LoggingMsg {
    private final String logMessage;
    private final String logClass;

    /**
     * Constructor to create a log message with a class and message.
     *
     * @param logClass    The name of the class generating the log.
     * @param logMessage  The actual log message.
     */
    public LoggingMsg(String logClass, String logMessage) {
        this.logMessage = logMessage;
        this.logClass = ClazzNameSanitizer.sanitize(logClass); // Sanitize the class name
    }

    /**
     * Static factory method for creating log messages.
     *
     * @param className The name of the class generating the log.
     * @param message   The actual log message.
     * @return A formatted log message string.
     */
    public static String create(String className, String message) {
        return new LoggingMsg(className, message).toString();
    };

    @Override
    public String toString() {
        return String.format("[%s] %s", logClass, logMessage);
    };
}
