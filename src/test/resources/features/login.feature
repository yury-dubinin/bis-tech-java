Feature: RabbitMQ Management Console Login

  Scenario: Successful login with valid credentials
    Given I am on the RabbitMQ login page
    When I login with username "guest" and password "guest"
    Then I should be redirected to the management console

  Scenario: Login rejected with invalid credentials
    Given I am on the RabbitMQ login page
    When I login with username "wrongUser" and password "wrongPassword"
    Then I should see an error message "Not_Authorized"
    And the login form should still be visible
