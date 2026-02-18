package com.beachape;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

public abstract class AbstractVirtualThreadAuthIT {

    private static final List<LogRecord> capturedLogs = new CopyOnWriteArrayList<>();
    private static Handler testHandler;

    private static final Pattern THREAD_PATTERN = Pattern.compile("\\[Thread:([^\\]]+)\\]");

    @BeforeAll
    static void setUp() {
        testHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                capturedLogs.add(record);
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };

        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(testHandler);
        rootLogger.setLevel(Level.INFO);
    }

    @AfterAll
    static void tearDown() {
        Logger.getLogger("").removeHandler(testHandler);
        capturedLogs.clear();
    }

    @BeforeEach
    void clearLogs() {
        capturedLogs.clear();
    }

    @Test
    public void testSayMyNameWithBasicAuthUsesVirtualThreads() {
        given()
                .auth().basic("testuser", "testpass")
                .when().get("/v1/hello/say-my-name")
                .then()
                .statusCode(200);

        assertFalse(capturedLogs.isEmpty(), "Expected logs to be captured");

        verifyVirtualThreadsInComponent("com.beachape.filters.ReqIdRequestFilter", "ReqIdRequestFilter");
        verifyVirtualThreadsInComponent("com.beachape.auth.MyAuthMechanism", "MyAuthMechanism");
        verifyVirtualThreadsInComponent("com.beachape.auth.MyAuthIdentityProvider", "MyAuthIdentityProvider");
        verifyVirtualThreadsInComponent("com.beachape.filters.ReqIdResponseFilter", "ReqIdResponseFilter");
    }

    private void verifyVirtualThreadsInComponent(String loggerName, String componentName) {
        List<String> componentThreadNames = capturedLogs.stream()
                .filter(record -> record.getLoggerName().equals(loggerName))
                .map(this::extractThreadNameFromMessage)
                .filter(threadName -> threadName != null)
                .collect(Collectors.toList());

        assertFalse(componentThreadNames.isEmpty(),
                "No logs with thread names found for component: " + componentName);

        boolean allVirtualThreads = componentThreadNames.stream()
                .allMatch(name -> name.startsWith("quarkus-virtual-thread-"));

        assertTrue(allVirtualThreads,
                String.format("Expected all threads in %s to be virtual threads, but found: %s",
                        componentName, componentThreadNames));

        System.out.println(componentName + " thread names: " + componentThreadNames);
    }

    private String extractThreadNameFromMessage(LogRecord record) {
        String message = record.getMessage();
        if (message == null) {
            return null;
        }

        var matcher = THREAD_PATTERN.matcher(message);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
