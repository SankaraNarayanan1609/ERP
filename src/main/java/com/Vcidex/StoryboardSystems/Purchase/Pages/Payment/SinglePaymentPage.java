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
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ─── Input Locators ─────────────────────────────────────────────
    private final By paymentDateInput     = By.xpath("//input[@formcontrolname='payment_date']");
    private final By paymentRemarksInput  = By.xpath("//textarea[@formcontrolname='payment_remarks']");
    private final By paymentNoteInput     = By.xpath("//textarea[@formcontrolname='payment_note']");
    private final By paymentModeDropdown  = By.xpath("//ng-select[@formcontrolname='payment_mode']");
    private final By submitBtn            = By.xpath("//button[contains(.,'Submit') and contains(@class,'btn-success')]");

    // Payment Amount cell input field in the table (first row assumed)
    private final By paymentAmountInput   = By.xpath("(//table[@id='singlepayment_list']//input[@type='text'])[1]");

    // Outstanding value for reference (same row)
    private final By outstandingValueCell = By.xpath("(//table[@id='singlepayment_list']//td)[7]"); // 7th <td> is Outstanding

    public SinglePaymentPage(WebDriver driver) {
        super(driver);
    }

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

        step(Layer.UI, "Fetch and match Outstanding Amount", () -> {
            String value = findElement(outstandingValueCell).getText().split("/")[0].trim(); // e.g., "7,770.18"
            findElement(paymentAmountInput).clear();
            findElement(paymentAmountInput).sendKeys(value);
            return null;
        });

        PerformanceLogger.end("Payment_fillForm");
    }

    // New overload method
    public void fillPaymentForm(PaymentData data, ExtentTest node) {
        fillPaymentForm(
                data.getPaymentDate().toString(),
                data.getPaymentRemarks(),
                data.getPaymentNote(),
                data.getPaymentMode(),
                node
        );
    }

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
