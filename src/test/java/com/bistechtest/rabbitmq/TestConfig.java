package com.bistechtest.rabbitmq;

/**
 * Central configuration for test infrastructure endpoints.
 *
 * <p>Values are read from system properties so they can be overridden at the
 * command line or in CI without touching the source:
 * <pre>
 *   mvn test -Damqp.uri=amqp://user:pass@broker:5672/%2f \
 *             -Drabbitmq.url=http://broker:15672/
 * </pre>
 */
public class TestConfig {

    public static final String AMQP_URI =
            System.getProperty("amqp.uri", "amqp://guest:guest@localhost:5672/%2f");

    public static final String MANAGEMENT_URL =
            System.getProperty("rabbitmq.url", "http://localhost:15672/");

    private TestConfig() {}
}
