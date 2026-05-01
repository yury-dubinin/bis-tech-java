package com.bistechtest.rabbitmq.cucumber;

import com.bistechtest.rabbitmq.pages.LoginPage;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LoginSteps {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private LoginPage loginPage;

    @Before
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(true).setSlowMo(500));
        context = browser.newContext();
        page = context.newPage();
        loginPage = new LoginPage(page);
    }

    @After
    public void tearDown() {
        if (context != null) context.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Given("I am on the RabbitMQ login page")
    public void iAmOnTheLoginPage() {
        loginPage.navigate();
    }

    @When("I login with username {string} and password {string}")
    public void iLoginWithCredentials(String username, String password) {
        loginPage.login(username, password);
    }

    @Then("I should be redirected to the management console")
    public void iShouldBeRedirectedToManagementConsole() {
        assertThat(page).hasURL(Pattern.compile(".*#/.*"));
    }

    @Then("I should see an error message {string}")
    public void iShouldSeeErrorMessage(String message) {
        assertThat(page.getByText(message)).isVisible();
    }

    @Then("the login form should still be visible")
    public void theLoginFormShouldStillBeVisible() {
        assertThat(page.locator("#username")).isVisible();
    }
}
