package com.bistechtest.rabbitmq;

import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.bistechtest.rabbitmq.pages.LoginPage;
import com.bistechtest.rabbitmq.pages.ManagementPage;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Tests 1 and 2 — RabbitMQ Management Console login behaviour.
 *
 * <ul>
 *   <li>Test 1: A user can log in with valid default credentials (guest / guest).</li>
 *   <li>Test 2: Incorrect credentials are rejected and an error is shown.</li>
 * </ul>
 *
 * <p>Both tests are isolated: each gets its own {@link com.microsoft.playwright.BrowserContext}
 * courtesy of {@link BaseTest#createContextAndPage()}.
 */
@DisplayName("RabbitMQ Management Login")
class LoginTest extends BaseTest {

    static class User {
        final String name;
        final String password;

        /** Defaults to the built-in {@code guest / guest} account. */
        User() {
            this("guest", "guest");
        }

        User(String name, String password) {
            this.name     = name;
            this.password = password;
        }
    }
    /**
     * Test 1 — Valid credentials.
     *
     * <p>Steps:
     * <ol>
     *   <li>Navigate to {@code http://localhost:15672/}.</li>
     *   <li>Enter the default {@code guest / guest} credentials and submit.</li>
     *   <li>Assert that the browser URL now contains the {@code #/} fragment, which
     *       indicates a successful login and redirect to the management overview.</li>
     * </ol>
     */
    @Test
    @DisplayName("Test 1 — successful login with default credentials")
    void testSuccessfulLogin() {
        User user = new User();
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate();
        loginPage.login(user.name, user.password);

        // After a successful login the management console redirects to /#/ (or /#/overview).
        // PlaywrightAssertions.assertThat(page).hasURL() retries until the condition is met.
        assertThat(page).hasURL(Pattern.compile(".*#/.*"));
        assertThat(page.locator("a[href='#/users/" + user.name + "']")).isVisible();
        loginPage.screenshot("01-logged-in.png");
    }

    /**
     * Test 2 — Invalid credentials.
     *
     * <p>Steps:
     * <ol>
     *   <li>Navigate to {@code http://localhost:15672/}.</li>
     *   <li>Enter deliberately wrong credentials and submit.</li>
     *   <li>Assert that the "Login failed" error message becomes visible and the
     *       username field is still present (no redirect occurred).</li>
     * </ol>
     */
    @Test
    @DisplayName("Test 2 — invalid credentials are rejected")
    void testInvalidLogin() {
        User user = new User("wrongUser", "wrongPassword");
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate();
        loginPage.login(user.name, user.password);

        // The management UI renders a "Not_Authorized" message on authentication failure.
        assertThat(page.getByText("Not_Authorized")).isVisible();

        // The login form must still be present — no redirect to the management console.
        assertThat(page.locator("#username")).isVisible();
        loginPage.screenshot("02-login-failed.png");
    }

    @Test
    @DisplayName("Test 4 — login and logout")
    void testLoginAndLogout() {
        User user = new User();
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate();
        loginPage.login(user.name, user.password);

        ManagementPage managementPage = new ManagementPage(page);
        managementPage.clickLogOut();

        // The login form must still be present.
        assertThat(page.locator("#username")).isVisible();
    }
}
