package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Payment.SinglePaymentPage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;

import java.util.List;

public class PaymentNavigator {
    private final WebDriver driver;
    private final NavigationManager nav;
    private final ExtentTest rootTest;

    public PaymentNavigator(WebDriver driver, NavigationManager nav, ExtentTest rootTest) {
        this.driver = driver;
        this.nav = nav;
        this.rootTest = rootTest;
    }

    /** Navigate to Single Payment and open modal for a specific vendor/invoice. */
    public SinglePaymentPage openSinglePayment(String vendorName, String invoiceRefNo) {
        try (ReportManager.Scope s = ReportManager.with(rootTest.createNode("🔍 Navigate to Single Payment"))) {

            // Go to Payment page (Purchase header → Payable module → Payment)
            MasterLogger.step(Layer.UI, "Navigate ▶ Purchase (header) → Payable → Payment", (Runnable) () -> {
                nav.open(
                        "Purchase",
                        "Payable",
                        "Payment",
                        d -> !d.findElements(By.xpath("//h3[contains(normalize-space(.),'Payment Summary')]")).isEmpty()
                );
            });

            // Click Add Payment
            By addPaymentBtn = By.xpath("//button[contains(normalize-space(.),'Add Payment')]");
            try {
                nav.waitUntilClickable(addPaymentBtn, "Add Payment").click();
            } catch (Exception e) {
                WebElement elm = driver.findElement(addPaymentBtn);
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView(true); arguments[0].click();", elm);
            }

            // Add Payment page up
            nav.waitUntilVisible(By.xpath("//h1[contains(normalize-space(.),'Add Payment')]"), "Add Payment");

            // Walk the rows to find vendor & invoice, then click Payment
            List<WebElement> allRows = driver.findElements(By.xpath("//table//tbody//tr"));
            int index = 0;

            while (index < allRows.size()) {
                WebElement row = allRows.get(index);
                String vendorText = row.getText().trim();

                if (!vendorText.contains(vendorName)) { index++; continue; }

                boolean hasChevron = !row.findElements(By.xpath(".//i[contains(@class,'bi-chevron-right')]")).isEmpty();
                if (hasChevron) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", row);
                    WebElement icon = row.findElement(By.xpath(".//i[contains(@class,'bi-chevron-right')]"));
                    try { icon.click(); } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", icon);
                    }

                    index++;
                    while (index < allRows.size()) {
                        WebElement invoiceRow = allRows.get(index);

                        boolean nextIsVendor = !invoiceRow.findElements(By.xpath(".//i[contains(@class,'bi-chevron-right')]")).isEmpty();
                        if (nextIsVendor) break;

                        List<WebElement> cells = invoiceRow.findElements(By.tagName("td"));
                        if (cells.size() >= 3) {
                            String refNo = cells.get(2).getText().trim();
                            if (refNo.equals(invoiceRefNo)) {
                                WebElement payBtn = invoiceRow.findElement(By.xpath(".//button[contains(normalize-space(.),'Payment')]"));
                                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payBtn);
                                try { payBtn.click(); } catch (ElementClickInterceptedException e) {
                                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", payBtn);
                                }
                                return new SinglePaymentPage(driver);
                            }
                        }
                        index++;
                    }
                } else {
                    index++;
                }
            }

            MasterLogger.warn("Invoice RefNo NOT found: " + invoiceRefNo);
            throw new IllegalStateException("Invoice RefNo not found: " + invoiceRefNo);
        }
    }

    // PaymentNavigator.java
    public static final class PayResult {
        private final String paymentNo;
        public PayResult(String paymentNo) { this.paymentNo = paymentNo; }
        public String paymentNo() { return paymentNo; }
    }

    public PayResult pay(com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData data) {
        try (ReportManager.Scope s = ReportManager.with(rootTest.createNode("💳 Make Payment for " + data.getInvoiceRefNo()))) {
            // We need vendor to expand the correct block on the list
            String vendor = data.getVendorName();
            if (vendor == null || vendor.isBlank()) {
                throw new IllegalArgumentException("PaymentData.vendorName is required to locate the invoice row.");
            }

            SinglePaymentPage page = openSinglePayment(vendor, data.getInvoiceRefNo());
            page.makePayment(data, rootTest);
            String paymentNo = page.submitAndCapture(rootTest);
            return new PayResult(paymentNo);
        }
    }
}