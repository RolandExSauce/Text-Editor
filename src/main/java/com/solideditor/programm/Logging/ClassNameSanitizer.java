package com.solideditor.programm.Logging;

import java.util.Map;

public class ClassNameSanitizer {

    private static final Map<String, String> classToModuleMap = Map.of(
            "IconnectApp", "API Module",
            "App", "App Module",
            "MainController", "Main-C-Module",
            "MenuBarController", "Menu-C-Module",
            "FileIOService", "File-S-Module",
            "TabManagerService", "Tab-S-Module",
            "LayoutSettings", "Settings-U-Module"
            );


    public static String sanitize(String className) {
        System.out.println("Sanitizing " + classToModuleMap.getOrDefault(className, "UnknownModule"));
        return classToModuleMap.getOrDefault(className, "UnknownModule");
    }

//    private static String getSimpleClassName(String fullClassName) {
//        if (fullClassName == null || fullClassName.isEmpty()) {
//            throw new IllegalArgumentException("Class name cannot be null or empty");
//        }
//        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
//    }
}