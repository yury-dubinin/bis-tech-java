package com.bistechtest.rabbitmq;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

/**
 * Base class for all Playwright tests.
 *
 * <p>Lifecycle:
 * <ul>
 *   <li>{@code @BeforeAll} — creates a single {@link Playwright} instance and launches Chromium once per test class.</li>
 *   <li>{@code @BeforeEach} — opens a fresh, isolated {@link BrowserContext} and {@link Page} before every test method.</li>
 *   <li>{@code @AfterEach}  — closes the context (and its page) after every test method.</li>
 *   <li>{@code @AfterAll}  — closes the browser and Playwright after all tests in the class have run.</li>
 * </ul>
 */
public abstract class BaseTest {

    static Playwright playwright;
    static Browser browser;

    BrowserContext context;
    Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(500)
        );
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext();
        page = context.newPage();
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }
}
