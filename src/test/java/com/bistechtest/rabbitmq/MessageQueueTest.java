package com.bistechtest.rabbitmq;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bistechtest.rabbitmq.pages.LoginPage;
import com.bistechtest.rabbitmq.pages.ManagementPage;
import com.bistechtest.rabbitmq.pages.QueueDetailPage;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.rabbitmq.client.amqp.Connection;
import com.rabbitmq.client.amqp.Environment;
import com.rabbitmq.client.amqp.Publisher;
import com.rabbitmq.client.amqp.impl.AmqpEnvironmentBuilder;

/**
 * Test 3 — Enqueue a message via the AMQP 1.0 API, then dequeue and verify it
 * using the RabbitMQ Management Console UI.
 *
 * <p>Steps:
 * <ol>
 *   <li>3.1 – Connect to the broker with the AMQP 1.0 Java client, declare a
 *       classic queue, and publish a known message body.</li>
 *   <li>3.2 – Log in to the management UI and click the "Queues and streams" tab.</li>
 *   <li>3.3 – Open the relevant queue from the queue list.</li>
 *   <li>3.4 – Expand the "Get messages" section, retrieve one message, and assert
 *       that its payload matches the published content.</li>
 * </ol>
 *
 * <p>AMQP setup and teardown use {@code @BeforeAll} / {@code @AfterAll} so the
 * broker connection is established once for the entire test class.  JUnit 5
 * guarantees that the parent-class {@code @BeforeAll} ({@link BaseTest#launchBrowser()})
 * runs first, so the Playwright browser is ready before AMQP setup begins.
 */
@DisplayName("RabbitMQ Message Queue — enqueue via API, dequeue via UI")
class MessageQueueTest extends BaseTest {

    private static final Logger log = LoggerFactory.getLogger(MessageQueueTest.class);

    static final String QUEUE_NAME   = "test-queue";
    static final String MESSAGE_BODY = "Hello RabbitMQ-" + java.util.UUID.randomUUID();
    static final String AMQP_URI     = "amqp://guest:guest@localhost:5672/%2f";

    static Environment amqpEnvironment;
    static Connection  amqpConnection;

    // -------------------------------------------------------------------------
    // 3.1  Enqueue a message using the RabbitMQ AMQP 1.0 API
    // -------------------------------------------------------------------------

    @BeforeAll
    static void enqueueMessage() throws Exception {
        // Build the AMQP 1.0 environment and open a connection to the broker.
        amqpEnvironment = new AmqpEnvironmentBuilder()
                .connectionSettings()
                .uri(AMQP_URI)
                .environmentBuilder()
                .build();

        amqpConnection = amqpEnvironment.connectionBuilder().build();

        // Ensure a clean slate: delete any leftover queue from a previous run,
        // then declare a fresh classic queue.  Classic queues are used because
        // the management UI's "Get messages" feature supports them reliably.
        try {
            amqpConnection.management().queueDelete(QUEUE_NAME);
        } catch (Exception ignored) {
            // Queue did not exist yet — that is fine.
        }

        amqpConnection.management()
                .queue(QUEUE_NAME)
                .classic()
                .queue()
                .declare();

        // Publish the test message and wait for the broker's ACCEPTED outcome.
        CountDownLatch publishLatch = new CountDownLatch(1);
        AtomicBoolean accepted = new AtomicBoolean(false);

        try (Publisher publisher = amqpConnection.publisherBuilder()
                .queue(QUEUE_NAME)
                .build()) {

            publisher.publish(
                    publisher.message(MESSAGE_BODY.getBytes(StandardCharsets.UTF_8)),
                    context -> {
                        accepted.set(context.status() == Publisher.Status.ACCEPTED);
                        publishLatch.countDown();
                    }
            );

            boolean completed = publishLatch.await(10, TimeUnit.SECONDS);
            assertTrue(completed, "Timed out waiting for publish confirmation from the broker.");
        }

        assertTrue(accepted.get(),
                "Broker did not accept the published message (status was not ACCEPTED).");

        log.info("[Setup] Published '{}' to queue '{}'.", MESSAGE_BODY, QUEUE_NAME);
    }

    @AfterAll
    static void cleanupAmqp() {
        if (amqpConnection != null) {
            try {
                amqpConnection.management().queueDelete(QUEUE_NAME);
            } catch (Exception ignored) {
                // Best-effort cleanup; do not fail teardown if the queue is already gone.
            }
            amqpConnection.close();
        }
        if (amqpEnvironment != null) {
            amqpEnvironment.close();
        }
    }

    // -------------------------------------------------------------------------
    // 3.2 – 3.4  Dequeue the message via the Management UI
    // -------------------------------------------------------------------------

    @Test
    @DisplayName("Test 3 — message published via AMQP 1.0 API is visible via Management UI")
    void testEnqueueAndDequeue() {
        // 3.2 — Log in and navigate to the Queues and Streams tab.
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate();
        loginPage.login("guest", "guest");

        ManagementPage managementPage = new ManagementPage(page);
        managementPage.clickQueuesAndStreamsTab();
        managementPage.screenshot("01-queues-list.png");

        // 3.3 — Open the target queue from the list.
        managementPage.clickQueue(QUEUE_NAME);
        managementPage.screenshot("02-queue-detail.png");

        // 3.4 — Expand "Get messages", request one message, and verify the payload.
        QueueDetailPage queueDetailPage = new QueueDetailPage(page);
        queueDetailPage.expandGetMessagesSection();
        queueDetailPage.setMessageCount(1);
        queueDetailPage.selectAckMode("ack_requeue_false");
        queueDetailPage.clickGetMessages();

        // The management UI renders the payload inside <pre class="msg-payload">.
        // assertThat(...).isVisible() has built-in retry / auto-wait.
        assertThat(page.locator("pre.msg-payload").first()).isVisible();
        queueDetailPage.screenshot("03-message-payload.png");

        String payload = queueDetailPage.getFirstMessagePayload();
        log.info("[testEnqueueAndDequeue] Received payload: '{}'", payload);
        assertTrue(payload.contains(MESSAGE_BODY),
                "Expected payload to contain \"" + MESSAGE_BODY + "\" but got: \"" + payload + "\"");
    }
}
