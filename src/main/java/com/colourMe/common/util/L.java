package com.colourMe.common.util;

import com.sun.javafx.scene.control.SizeLimitedList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class L {
    private static boolean initialized = false;
    private static DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.mmm");
    private static Logger logger = Logger.getLogger("Test");

    public static void initLogging() {
        if (!initialized) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tc] [%4$s] : %5$s %6$s%n");
            logger.setLevel(Level.INFO);
            initialized = true;
        }
    }

    private static String logFormat(String className, String message){
        return String.format("[%s]: %s", className, message);
    }

    public static <T> void log(Level level, T object, String message) {
        String className = object.getClass().getName();
        logger.log(level, logFormat(className, message));
    }

    public static <T> void log(Level level, T object, String funcName, String message) {
        String className = object.getClass().getName();
        logger.logp(level, className, funcName, message);
    }

    public static <T> void severe(T object, String message){
        log(Level.SEVERE, object, message);
    }

    public static <T> void warn(T object, String message) {
        log(Level.WARNING, object, message);
    }

    public static <T> void info(T object, String message) {
        log(Level.INFO, object, message);
    }

    public static<T> void logArray(Level level, T[] array) {
        Arrays.asList(array).forEach(x -> logValue(level, x));
    }

    public static <T> void logValue(Level level, T object) {
        // TODO: Serialize and log value
    }
}
