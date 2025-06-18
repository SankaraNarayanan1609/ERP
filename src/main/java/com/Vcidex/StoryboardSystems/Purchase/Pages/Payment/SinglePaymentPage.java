/**
 * Page Object representing the UI for performing a single payment against an invoice.
 * It handles filling out payment fields and submitting the form.
 *
 * This class is used during automation of payment flow after invoice generation.
 */

package com.Vcidex.StoryboardSystems.Purchase.Pages.Payment;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import java.time.format.DateTimeFormatter;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;

public class SinglePaymentPage extends BasePage {

    // Date formatter for display/debug (not actively used here)
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ─── UI Locators ──────────────────────────────────────────────────────────

    // Form fields for entering payment details
    private final By paymentDateInput     = By.xpath("//input[@formcontrolname='payment_date']");
    private final By paymentRemarksInput  = By.xpath("//textarea[@formcontrolname='payment_remarks']");
    private final By paymentNoteInput     = By.xpath("//textarea[@formcontrolname='payment_note']");
    private final By paymentModeDropdown  = By.xpath("//ng-select[@formcontrolname='payment_mode']");

    // Table field for entering the actual payment amount (assumes 1st row)
    private final By paymentAmountInput   = By.xpath("(//table[@id='singlepayment_list']//input[@type='text'])[1]");

    // Table cell showing the outstanding value to be paid (7th column)
    private final By outstandingValueCell = By.xpath("(//table[@id='singlepayment_list']//td)[7]");

    // Button to submit payment
    private final By submitBtn            = By.xpath("//button[contains(.,'Submit') and contains(@class,'btn-success')]");

    /**
     * Constructor that initializes this Page Object with the WebDriver.
     *
     * @param driver Selenium WebDriver instance
     */
    public SinglePaymentPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Fills the payment form using direct string values.
     *
     * @param date    payment date as string
     * @param remarks short description or reason
     * @param note    additional note
     * @param mode    payment mode (e.g., Cash)
     * @param node    ExtentTest node for logging
     */
    public void fillPaymentForm(String date, String remarks, String note, String mode, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("Payment_fillForm");

        step(Layer.UI, "Fill Payment Date: " + date, () -> {
            type(paymentDateInput, date, "Payment Date");
            return null;
        });

        step(Layer.UI, "Fill Payment Remarks: " + remarks, () -> {
            type(paymentRemarksInput, remarks, "Payment Remarks");
            return null;
        });

        step(Layer.UI, "Fill Payment Note: " + note, () -> {
            type(paymentNoteInput, note, "Payment Note");
            return null;
        });

        step(Layer.UI, "Select Payment Mode: " + mode, () -> {
            selectFromNgSelect("payment_mode", mode);
            return null;
        });

        // Set the payment amount equal to the outstanding amount (displayed in table)
        step(Layer.UI, "Fetch and match Outstanding Amount", () -> {
            String value = findElement(outstandingValueCell).getText().split("/")[0].trim(); // e.g., "7,770.18"
            findElement(paymentAmountInput).clear();
            findElement(paymentAmountInput).sendKeys(value);
            return null;
        });

        PerformanceLogger.end("Payment_fillForm");
    }

    /**
     * Overloaded method to fill the form using a POJO object (PaymentData).
     * Helps in clean test writing with data factories.
     *
     * @param data PaymentData object containing all fields
     * @param node ExtentTest node for reporting
     */
    public void fillPaymentForm(PaymentData data, ExtentTest node) {
        fillPaymentForm(
                data.getPaymentDate().toString(),
                data.getPaymentRemarks(),
                data.getPaymentNote(),
                data.getPaymentMode(),
                node
        );
    }

    /**
     * Submits the payment form and waits for any network/spinner to clear.
     *
     * @param node ExtentTest logger node
     */
    public void submitPayment(ExtentTest node) {
        ReportManager.setTest(node);
        step(Layer.UI, "Click Submit", () -> {
            click(submitBtn, "Submit Button");
            return null;
        });
        waitForAngularRequestsToFinish();
        waitForOverlayClear();
    }
}