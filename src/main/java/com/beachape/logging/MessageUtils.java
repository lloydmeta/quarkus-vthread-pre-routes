package com.beachape.logging;

public class MessageUtils {

    /**
     * Utility method to format a log message with the thread name.
     *
     * @param message the log message
     * @return formatted log message with thread name
     */
    public static String formatMessageWithThread(String message) {
        return String.format("[Thread:%s] %s", Thread.currentThread().getName(), message);
    }
}
