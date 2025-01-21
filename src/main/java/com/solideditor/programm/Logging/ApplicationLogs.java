package com.solideditor.programm.Logging;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import java.io.File;


//to trace logs in file more effortlessly
public class ApplicationLogs {
    private static final String APPDATA_DIR_WIN = "\\AppData\\Local\\SolidEdit\\logs";
    private static final String APPDATA_DIR_MAC = "/Library/Application Support/SolidEdit/logs";
    private  static  final String APPDATA_DIR_UNIX_OR_OTHERS = "/.local/share/SolidEdit/logs";

    @Getter
    private static final Logger rootLogger;

    @Getter
    private static final Logger errorLogger;

    static {
        //platform-specific log directory
        String logDir = getLogDirectory();

        // Create directory if it doesn't exist
        File logFolder = new File(logDir);
        if (!logFolder.exists()) {
            if (logFolder.mkdirs()) {
                System.out.println("Log DIR created: " + logDir);
            } else {
                System.err.println("Failed to create log DIR: " + logDir);
            };
        };

        System.setProperty("LOG_DIR", logDir); // Pass the log directory to Logback
        //init loggers
        rootLogger = LoggerFactory.getLogger("GlobalLogger");
        errorLogger = LoggerFactory.getLogger("ERROR_LOGGER");
    }

    private ApplicationLogs() {
        // prevent instantiation
    };

    private static String getLogDirectory() {
        return getOSLogLocation(APPDATA_DIR_WIN, APPDATA_DIR_MAC, APPDATA_DIR_UNIX_OR_OTHERS);
    };

    public static String getOSLogLocation(String appdataDirWin, String appdataDirMac, String appdataDirUnixOrOthers) {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return userHome + appdataDirWin;
        } else if (os.contains("mac")) {
            return userHome + appdataDirMac;
        } else { // assume linux, unix or other os types
            return userHome + appdataDirUnixOrOthers;
        }
    };

}
