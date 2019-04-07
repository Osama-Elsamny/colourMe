package com.colourMe.common.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

public class U {
    private static Gson gson = new Gson();

    public static void handleExceptionBase(Exception ex) {
        System.err.println(ex.getMessage());
        ex.printStackTrace();
    }

    public static void handleExceptionBase(Logger logger, Exception ex){
        logger.warning(ex.getMessage());
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        logger.warning("\n" + pw.toString());
    }

    public static boolean sleep(long millis){
        boolean interrupted = false;
        try {
            Thread.sleep(millis);
            interrupted = true;
        } catch(Exception ex) {
            handleExceptionBase(ex);
        }
        return interrupted;
    }

    public static boolean wrapInTryCatch(Runnable function) {
        boolean successful = false;
        try {
            function.run();
            successful = true;
        } catch (Exception ex) {
            handleExceptionBase(ex);
        }
        return successful;
    }

    public static boolean wrapInTryCatch(Logger logger, Runnable function) {
        boolean successful = false;
        try {
            function.run();
            successful = true;
        } catch (Exception ex) {
            handleExceptionBase(logger, ex);
        }
        return successful;
    }

    public static <T> T wrapInTryCatch(Callable<T> function) {
        try {
            return function.call();
        } catch (Exception ex) {
            handleExceptionBase(ex);
        }
        return null;
    }

    public static <T> JsonElement toJsonElement(T obj) {
        return wrapInTryCatch(() -> gson.toJsonTree(obj));
    }

    public static <T> JsonArray toJsonArray(T obj) {
        return toJsonElement(obj).getAsJsonArray();
    }

    public static <T> JsonObject toJsonObject(T obj) {
        return toJsonElement(obj).getAsJsonObject();
    }

    public static <T> String json(T obj) {
        return gson.toJson(obj);
    }

    public static Gson gson() {
        return gson;
    }

    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static <T> boolean listsAreEqual(List<T> list1, List<T> list2) {
        boolean equal = list1.size() == list2.size();
        if (equal) {
            Iterator iter = list2.iterator();
            for(T obj : list1) {
                equal = equal && obj.equals(iter.next());
            }
        }
        return equal;
    }
}
