package com.colourMe.common.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {
    private static final String LOG_FORMAT
            = "[%4$s] [%1$tF %1$tT.%1$tL] [%2$s]: %5$s %6$s%n";
    private static boolean initialized = false;
    private static Level defaultLevel = Level.INFO;

    public static void initLogging() {
        if (!initialized) {
            System.setProperty("java.util.logging.SimpleFormatter.format", LOG_FORMAT);
            initialized = true;
        }
    }

    public static <T> Logger get(T object) {
        Logger logger = Logger.getLogger(object.getClass().getName());
        logger.setLevel(defaultLevel);
        return logger;
    }

    public static void setDefaultLevel(Level level) {
        defaultLevel = level;
    }
}
