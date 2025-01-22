package com.solideditor.programm.Logging;
import java.util.Map;


//sanitizer class
public class ClazzNameSanitizer {
    private static final Map<String, String> classToModuleMap = Map.of(
            "IConnectApp", "API Module",
            "App", "App Module",
            "MainController", "Main-C-Module",
            "MenuBarController", "Menu-C-Module",
            "DialogController", "Dialog-C-Module",
            "FileIOService", "File-S-Module",
            "TabManagerService", "Tab-S-Module",
            "LayoutSettings", "Settings-U-Module"
    );

    //this makes sure class names are not revealed in logs with their true name
    public static String sanitize(String className) {
        return classToModuleMap.getOrDefault(className, "UnknownModule");
    };
};