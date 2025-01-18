package com.solideditor.programm.temp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class LogUtil {
    private static final Logger logger;

    static {
        // Determine platform-specific log directory
        String logDir = getLogDirectory();

        // Create directory if it doesn't exist
        File logFolder = new File(logDir);
        if (!logFolder.exists()) {
            if (logFolder.mkdirs()) {
                System.out.println("Log directory created: " + logDir);
            } else {
                System.err.println("Failed to create log directory: " + logDir);
            }
        }

        // Initialize logger
        System.setProperty("LOG_DIR", logDir); // Pass the log directory to Logback
        logger = LoggerFactory.getLogger("GlobalLogger");
    }

    private LogUtil() {
        // Prevent instantiation
    }

    public static Logger getLogger() {
        return logger;
    }

    private static String getLogDirectory() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return userHome + "\\AppData\\Local\\YourAppName\\logs";
        } else if (os.contains("mac")) {
            return userHome + "/Library/Application Support/YourAppName/logs";
        } else { // Assume Linux or other Unix-like OS
            return userHome + "/.local/share/YourAppName/logs";
        }
    }
}



