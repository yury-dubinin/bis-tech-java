package com.bistechtest.rabbitmq.pages;

import java.nio.file.Paths;

import com.microsoft.playwright.Page;

/**
 * Abstract base class for all page objects.
 *
 * <p>Holds the shared {@link Page} reference and provides a
 * {@link #screenshot(String)} helper that saves a full-page PNG to
 * {@code target/screenshots/<filename>}.
 */
public abstract class BasePage {

    protected final Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    /**
     * Takes a full-page screenshot and writes it to
     * {@code target/screenshots/<filename>}.
     *
     * @param filename the file name, e.g. {@code "01-logged-in.png"}
     */
    public void screenshot(String filename) {
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get("target/screenshots/" + filename))
                .setFullPage(true));
    }
}
