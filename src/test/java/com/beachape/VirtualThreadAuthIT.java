package com.beachape;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;

@QuarkusTest
public class VirtualThreadAuthIT {

    private static final List<LogRecordWithThread> capturedLogs = new CopyOnWriteArrayList<>();
    private static Handler testHandler;

    // Custom class to store log record with the actual thread name
    record LogRecordWithThread(LogRecord record, String threadName) {
    }

    @BeforeAll
    static void setUp() {
        testHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                // Capture the actual thread name at the moment of logging
                String currentThreadName = Thread.currentThread().getName();
                capturedLogs.add(new LogRecordWithThread(record, currentThreadName));
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
        // Create basic auth header
        String credentials = "testuser:testpass";
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        given()
                .header("Authorization", "Basic " + encodedCredentials)
                .when().get("/v1/hello/say-my-name")
                .then()
                .statusCode(200);

        // Verify that logs were captured
        assertFalse(capturedLogs.isEmpty(), "Expected logs to be captured");

        // Filter logs by component and check for virtual threads
        verifyVirtualThreadsInComponent("com.beachape.filters.ReqIdRequestFilter", "ReqIdRequestFilter");
        verifyVirtualThreadsInComponent("com.beachape.auth.MyAuthMechanism", "MyAuthMechanism");
        verifyVirtualThreadsInComponent("com.beachape.auth.MyAuthIdentityProvider", "MyAuthIdentityProvider");
        verifyVirtualThreadsInComponent("com.beachape.filters.ReqIdResponseFilter", "ReqIdResponseFilter");
    }

    private void verifyVirtualThreadsInComponent(String loggerName, String componentName) {
        List<String> componentThreadNames = capturedLogs.stream()
                .filter(entry -> entry.record().getLoggerName().equals(loggerName))
                .map(LogRecordWithThread::threadName)
                .collect(Collectors.toList());

        assertFalse(componentThreadNames.isEmpty(),
                "No logs found for component: " + componentName);

        // Virtual threads typically have names containing "Virtual" or matching
        // patterns like "VirtualThread[#123]"
        boolean allVirtualThreads = componentThreadNames.stream()
                .allMatch(name -> name.toLowerCase().contains("virtual"));

        assertTrue(allVirtualThreads,
                String.format("Expected all threads in %s to be virtual threads, but found: %s",
                        componentName, componentThreadNames));

        System.out.println(componentName + " thread names: " + componentThreadNames);
    }
}