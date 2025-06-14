package com.beachape;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import static io.restassured.RestAssured.given;

@QuarkusTest
class GreetingResourceTest {

    // Pattern to extract thread name from formatted messages like
    // "[Thread:VirtualThread[#123]] message"
    private static final Pattern THREAD_PATTERN = Pattern.compile("\\[Thread:([^\\]]+)\\]");

    @Test
    void testHelloEndpoint() {
        given()
                .when().get("/v1/hello")
                .then()
                .statusCode(200)
                .body(is("Hello from Quarkus REST"));
    }

    @Test
    void testHelloEndpointUsesVirtualThreads() {
        var capturedLogs = new CopyOnWriteArrayList<String>();
        var capturedThreadNames = new CopyOnWriteArrayList<String>();

        // Create a custom handler that captures thread names from log messages
        var customHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record.getMessage() != null
                        && record.getMessage().contains("Handling hello within GreetingResource")) {
                    capturedLogs.add(record.getMessage());

                    // Extract thread name from the formatted message
                    String threadName = extractThreadNameFromMessage(record.getMessage());
                    if (threadName != null) {
                        capturedThreadNames.add(threadName);
                    }
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        };

        var julLogger = Logger.getLogger("com.beachape.routes.hello.GreetingResource");
        julLogger.addHandler(customHandler);
        julLogger.setLevel(Level.INFO);

        try {
            // Make multiple requests to ensure we capture logs
            for (int i = 0; i < 3; i++) {
                given()
                        .when().get("/v1/hello")
                        .then()
                        .statusCode(200)
                        .body(is("Hello from Quarkus REST"));
            }

            // Wait a bit for async logging
            Thread.sleep(100);

            // Verify we captured some logs
            assertTrue(!capturedLogs.isEmpty(), "Should have captured some log entries");

            // Check if any thread names indicate virtual threads
            boolean hasVirtualThread = capturedThreadNames.stream()
                    .anyMatch(threadName -> threadName.startsWith("quarkus-virtual-thread-"));

            assertTrue(hasVirtualThread,
                    "Expected at least one log entry from a virtual thread. Captured threads: " + capturedThreadNames);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            julLogger.removeHandler(customHandler);
        }
    }

    private String extractThreadNameFromMessage(String message) {
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