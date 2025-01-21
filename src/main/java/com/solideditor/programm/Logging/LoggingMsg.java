package com.solideditor.programm.Logging;


// Log message with the corresponding class
public class LoggingMsg {
    private final String logMsg;
    private final String logClass;

    /**
     * Constructor to create a log message with a class and message.
     *
     * @param logClass    The name of the class generating the log.
     * @param logMsg  The actual log message.
     */
    public LoggingMsg(String logClass, String logMsg) {
        this.logClass = ClazzNameSanitizer.sanitize(logClass); // Sanitize the class name
        this.logMsg = logMsg;
    };

    /**
     * Static factory method for creating log messages.
     *
     * @param className The name of the class generating the log.
     * @param logMsg   The actual log message.
     * @return A formatted log message string.
     */

    public static String create(String className, String logMsg) {
        return new LoggingMsg(className, logMsg).toString();
    };

    @Override
    public String toString() {
        return String.format("[%s] %s", logClass, logMsg);
    };
}
