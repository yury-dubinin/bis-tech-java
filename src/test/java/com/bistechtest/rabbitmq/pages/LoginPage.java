package com.bistechtest.rabbitmq.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page Object for the RabbitMQ Management login form at {@code http://localhost:15672/}.
 */
public class LoginPage extends BasePage {

    private static final String URL = "http://localhost:15672/";

    public LoginPage(Page page) {
        super(page);
    }

    /** Navigates the browser to the RabbitMQ Management login page. */
    public void navigate() {
        page.navigate(URL);
    }

    /**
     * Fills in the login form and submits it.
     *
     * @param username the RabbitMQ username
     * @param password the RabbitMQ password
     */
    public void login(String username, String password) {
        page.locator("#username").fill(username);
        page.locator("#password").fill(password);
        page.locator("input[type='submit']").click();
    }

    /**
     * Returns the locator for the login-failure error message that appears when
     * credentials are rejected.
     */
    public Locator getLoginErrorLocator() {
        return page.getByText("Login failed");
    }
}
