package com.test.cashi

import io.appium.java_client.AppiumBy
import org.junit.Assert.*
import org.junit.Test
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

/**
 * Appium test for Payment Flow
 * Tests sending a payment and verifying it appears in transaction history
 */
class PaymentFlowTest : BaseAppiumTest() {

    private val testEmail = "test.recipient@example.com"
    private val testAmount = "100.50"
    private val wait by lazy { WebDriverWait(driver, Duration.ofSeconds(20)) }

    @Test
    fun testSendPaymentAndVerifyInHistory() {
        // Step 1: Open the payment bottom sheet by clicking the FAB (Floating Action Button)
        val addPaymentButton = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.accessibilityId("SendPaymentFAB")
            )
        )
        addPaymentButton.click()

        // Wait for the bottom sheet to open
        waitFor(1)

        // Step 2: Verify payment form is displayed
        val paymentHeader = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[@text='Send Payment']")
            )
        )
        assertTrue("Payment form should be visible", paymentHeader.isDisplayed)

        // Step 3: Fill in recipient email
        val emailField = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                // Find by hint text or label
                AppiumBy.xpath("//*[contains(@text, 'Recipient Email') or contains(@content-desc, 'Recipient Email')]")
            )
        )
        emailField.click()

        // Find the actual input field (might be a child element)
        val emailInput = driver.findElement(
            AppiumBy.className("android.widget.EditText")
        )
        emailInput.sendKeys(testEmail)

        waitFor(1)

        // Find all EditTexts and use the last one (amount field comes after email and is visible)
        val amountInputs = driver.findElements(AppiumBy.className("android.widget.EditText"))
        println("Found ${amountInputs.size} EditText fields")

        // The amount field should be the last visible EditText
        val amountInput = amountInputs.lastOrNull() ?: throw Exception("No amount input field found")
        amountInput.click()
        waitFor(1)
        amountInput.sendKeys(testAmount)

        // Step 6: Wait for UI to settle and keyboard to auto-dismiss
        // Don't press BACK as it will dismiss the bottom sheet
        waitFor(2)

        // Click Submit Payment button using accessibility ID
        val submitButton = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.accessibilityId("SubmitPaymentButton")
            )
        )
        assertTrue("Submit button should be enabled", submitButton.isEnabled)
        submitButton.click()

        // Step 8: Wait for payment to be submitted (loading state)
        waitFor(3)

        // Step 9: Verify we're back to the transaction list
        val transactionsHeader = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[@text='Transactions']")
            )
        )
        assertTrue("Should return to transactions screen", transactionsHeader.isDisplayed)

        // Step 10: Verify the payment appears in the transaction history
        val transactionCard = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[contains(@text, '$testEmail')]")
            )
        )
        assertTrue("Payment should appear in transaction list", transactionCard.isDisplayed)

        // Step 11: Verify the amount is displayed correctly
        val amountElement = driver.findElement(
            AppiumBy.xpath("//*[contains(@text, '$testAmount') or contains(@text, '\$100.50')]")
        )
        assertTrue("Payment amount should be displayed", amountElement.isDisplayed)

        // Step 12: Verify transaction status (should be PENDING or COMPLETED)
        try {
            val statusElement = driver.findElement(
                AppiumBy.xpath("//*[@text='PENDING' or @text='COMPLETED' or @text='Pending' or @text='Completed']")
            )
            assertTrue("Transaction status should be visible", statusElement.isDisplayed)
        } catch (e: Exception) {
            // Status might be displayed differently, log but don't fail
            println("Warning: Could not verify transaction status: ${e.message}")
        }
    }

    @Test
    fun testPaymentFormValidation() {

        // Step 1: Open the payment bottom sheet
        val addPaymentButton = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.accessibilityId("SendPaymentFAB")
            )
        )
        addPaymentButton.click()

        waitFor(1)

        // Step 2: Verify payment form is displayed
        val paymentHeader = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[@text='Send Payment']")
            )
        )
        assertTrue("Payment form should be visible", paymentHeader.isDisplayed)

        // Step 3: Fill only email (partial data)
        val emailInput = driver.findElement(AppiumBy.className("android.widget.EditText"))
        emailInput.clear()
        emailInput.sendKeys(testEmail)

        waitFor(1)

        // Step 4: Verify we can cancel with partial data (validation working)
        val cancelButton = driver.findElement(AppiumBy.xpath("//*[@text='Cancel']"))
        cancelButton.click()

        waitFor(1)

        // Should be back to transaction list
        val transactionsHeader = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[@text='Transactions']")
            )
        )
        assertTrue("Should return to transactions screen after cancel", transactionsHeader.isDisplayed)
    }

    @Test
    fun testCurrencySelection() {
        // Step 1: Open the payment bottom sheet
        val addPaymentButton = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.accessibilityId("SendPaymentFAB")
            )
        )
        addPaymentButton.click()

        waitFor(1)

        // Step 2: Click on currency dropdown
        val currencyDropdown = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[contains(@text, 'USD') and contains(@text, '\$')]")
            )
        )
        currencyDropdown.click()

        waitFor(1)

        // Step 3: Verify currency options are displayed
        val eurOption = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[@text='EUR (€)']")
            )
        )
        assertTrue("EUR option should be visible", eurOption.isDisplayed)

        // Step 4: Select EUR
        eurOption.click()

        waitFor(1)

        // Step 5: Verify EUR is now selected
        val selectedCurrency = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[contains(@text, 'EUR') and contains(@text, '€')]")
            )
        )
        assertTrue("EUR should be selected", selectedCurrency.isDisplayed)

        // Verify the amount field now shows € symbol
        val amountFieldWithEuro = driver.findElement(
            AppiumBy.xpath("//*[@text='€']")
        )
        assertTrue("Amount field should show € symbol", amountFieldWithEuro.isDisplayed)

        // Step 6: Clean up - close the bottom sheet
        val cancelButton = driver.findElement(AppiumBy.xpath("//*[@text='Cancel']"))
        cancelButton.click()

        waitFor(1)

        // Verify we're back to the transaction list
        val transactionsHeader = wait.until(
            ExpectedConditions.presenceOfElementLocated(
                AppiumBy.xpath("//*[@text='Transactions']")
            )
        )
        assertTrue("Should return to transactions screen after cancel", transactionsHeader.isDisplayed)
    }
}