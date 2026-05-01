package com.bistechtest.rabbitmq.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

/**
 * Page Object for the RabbitMQ Management Console main (overview) page.
 * Used after a successful login to verify the logged-in state and navigate
 * between top-level sections.
 */
public class ManagementPage extends BasePage {

    public ManagementPage(Page page) {
        super(page);
    }

    /**
     * Returns {@code true} when the browser URL contains the {@code #/} fragment that
     * the management console uses for all authenticated pages.
     */
    public boolean isLoggedIn() {
        return page.url().contains("#/");
    }

    /**
     * Clicks the "Queues and streams" top-level navigation tab and waits for the
     * queue list page to load.
     */
    public void clickQueuesAndStreamsTab() {
        page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName("Queues and streams")).click();
    }

    /**
     * Clicks the link for the given queue name in the queue list table.
     * Uses {@code .first()} to handle scenarios where the name appears in both a
     * breadcrumb and the table row.
     *
     * @param queueName the exact name of the queue to open
     */
    public void clickQueue(String queueName) {
        page.getByRole(AriaRole.LINK,
                new Page.GetByRoleOptions().setName(queueName)).first().click();
    }
}
