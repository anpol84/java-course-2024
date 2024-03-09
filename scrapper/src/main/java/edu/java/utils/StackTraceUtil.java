package edu.java.utils;

import java.util.ArrayList;
import java.util.List;


public class StackTraceUtil {
    private StackTraceUtil() {

    }

    public static List<String> getStringStakeTrace(Exception ex) {
        List<String> stackTrace = new ArrayList<>();
        for (var el : ex.getStackTrace()) {
            stackTrace.add(el.toString());
        }
        return stackTrace;
    }
}
