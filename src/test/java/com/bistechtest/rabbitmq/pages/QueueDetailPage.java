package com.bistechtest.rabbitmq.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the RabbitMQ Management queue detail page
 * (e.g. {@code http://localhost:15672/#/queues/%2F/<queue-name>}).
 *
 * <p>Provides an API for the "Get messages" section which allows dequeuing
 * messages directly from the management UI.
 */
public class QueueDetailPage extends BasePage {

    public QueueDetailPage(Page page) {
        super(page);
    }

    /**
     * Expands the "Get messages" collapsible section on the queue detail page.
     * The section header acts as a toggle; clicking it reveals the form when it
     * is currently collapsed.
     */
    public void expandGetMessagesSection() {
        // The section header <h2> text acts as a click-to-expand toggle.
        page.locator("h2").filter(new Locator.FilterOptions().setHasText("Get messages")).click();
    }

    /**
     * Sets how many messages to retrieve in the "Get messages" form.
     *
     * @param count number of messages to dequeue (typically 1 for tests)
     */
    public void setMessageCount(int count) {
        Locator countInput = page.locator("input[name='count']");
        countInput.fill(String.valueOf(count));
    }

    /**
     * Selects an ack mode from the {@code select[name="ackmode"]} dropdown.
     *
     * @param value the {@code <option value>} to select, e.g. {@code "ack_requeue_false"}
     *              for "Automatic ack"
     */
    public void selectAckMode(String value) {
        page.locator("select[name='ackmode']").selectOption(value);
    }

    /**
     * Submits the "Get messages" form by clicking the "Get Message(s)" button.
     * Playwright's auto-wait ensures the button is actionable before clicking.
     */
    public void clickGetMessages() {
        page.locator("input[value='Get Message(s)']").click();
    }

    /**
     * Returns the text content of the first message payload displayed after
     * "Get messages" has been executed.
     *
     * <p>The RabbitMQ management UI renders each message payload inside
     * {@code <pre class="msg-payload">} within the results table.  The locator
     * waits until at least one such element becomes visible in the DOM.
     *
     * @return the raw text of the first message payload
     */
    public String getFirstMessagePayload() {
        Locator payload = page.locator("pre.msg-payload").first();
        payload.waitFor();
        return payload.textContent();
    }
}
