package com.solideditor.programm.Logging;

import lombok.Getter;

public class LogMsg {

    //@Getter
    private final String logMessage;
    private final String logClass;


    public LogMsg(String logMessage, String logClass) {
        this.logMessage = logMessage;
        System.out.println("LogMsg: " + logClass);
        this.logClass = ClassNameSanitizer.sanitize(logClass); // Sanitize the class name

    }

    @Override
    public String toString() {
        return String.format("[%s] %s", logMessage, logClass);
    }
}

