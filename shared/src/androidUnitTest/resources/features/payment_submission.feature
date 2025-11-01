Feature: Payment Submission
  As a user
  I want to submit payments
  So that I can send money to recipients

  Scenario: Successfully submit a valid payment
    Given a user enters valid payment details
      | recipientEmail   | amount | currency |
      | user@example.com | 100.00 | USD      |
    When they submit the payment
    Then the payment is processed successfully
    And the payment is saved to Firestore

  Scenario: Submit payment with EUR currency
    Given a user enters valid payment details
      | recipientEmail     | amount | currency |
      | recipient@test.com | 250.50 | EUR      |
    When they submit the payment
    Then the payment is processed successfully
    And the payment is saved to Firestore

  Scenario: Fail to submit payment with invalid email
    Given a user enters payment details with invalid email
      | recipientEmail | amount | currency |
      | invalid-email  | 100.00 | USD      |
    When they submit the payment
    Then the payment submission fails
    And an error message "Invalid email format" is shown

  Scenario: Fail to submit payment with empty email
    Given a user enters payment details with empty email
      | recipientEmail | amount | currency |
      |                | 100.00 | USD      |
    When they submit the payment
    Then the payment submission fails
    And an error message "Email cannot be empty" is shown

  Scenario: Fail to submit payment with zero amount
    Given a user enters payment details with zero amount
      | recipientEmail   | amount | currency |
      | user@example.com | 0.00   | USD      |
    When they submit the payment
    Then the payment submission fails
    And an error message "Amount must be greater than 0" is shown

  Scenario: Fail to submit payment with negative amount
    Given a user enters payment details with negative amount
      | recipientEmail   | amount  | currency |
      | user@example.com | -50.00  | USD      |
    When they submit the payment
    Then the payment submission fails
    And an error message "Amount must be greater than 0" is shown

  Scenario: Fail to submit payment with amount exceeding maximum
    Given a user enters payment details with excessive amount
      | recipientEmail   | amount     | currency |
      | user@example.com | 2000000.00 | USD      |
    When they submit the payment
    Then the payment submission fails
    And an error message "Amount exceeds maximum limit" is shown

  Scenario: Network error during payment submission
    Given a user enters valid payment details
      | recipientEmail   | amount | currency |
      | user@example.com | 100.00 | USD      |
    And the network is unavailable
    When they submit the payment
    Then the payment submission fails
    And an error message "Network error" is shown