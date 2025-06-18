package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Payment.SinglePaymentPage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
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

    /**
     * Navigate to Single Payment section and open the modal for a specific vendor invoice.
     */
    public SinglePaymentPage openSinglePayment(String vendorName, String invoiceRefNo) {
        ExtentTest node = rootTest.createNode("🔍 Navigate to Single Payment");
        ReportManager.setTest(node);

        MasterLogger.step(Layer.UI, "Navigate to Payment page", () -> {
            nav.goTo("Purchase", "Payable", "Payment");
            return null;
        });

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h3[contains(text(),'Payment Summary')]")));

        WebElement addPaymentBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(text(),'Add Payment')]")));
        addPaymentBtn.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h1[contains(text(),'Add Payment')]")));

        // Get all rows in sequence
        List<WebElement> allRows = driver.findElements(By.xpath("//table//tbody//tr"));
        int index = 0;

        while (index < allRows.size()) {
            WebElement row = allRows.get(index);

            String vendorText = row.getText().trim();
            if (!vendorText.contains(vendorName)) {
                index++;
                continue;
            }

            boolean hasChevron = !row.findElements(By.xpath(".//i[contains(@class,'bi-chevron-right')]")).isEmpty();
            if (hasChevron) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", row);
                WebElement icon = row.findElement(By.xpath(".//i[contains(@class,'bi-chevron-right')]"));
                icon.click();

                try {
                    Thread.sleep(1000); // Allow invoice rows to load
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting for invoice rows to load", e);
                }

                index++;
                while (index < allRows.size()) {
                    WebElement invoiceRow = allRows.get(index);

                    // If we reach another vendor row, break
                    boolean nextIsVendor = !invoiceRow.findElements(By.xpath(".//i[contains(@class,'bi-chevron-right')]")).isEmpty();
                    if (nextIsVendor) break;

                    List<WebElement> cells = invoiceRow.findElements(By.tagName("td"));
                    if (cells.size() >= 3) {
                        String refNo = cells.get(2).getText().trim();
                        if (refNo.equals(invoiceRefNo)) {
                            WebElement payBtn = invoiceRow.findElement(
                                    By.xpath(".//button[contains(text(),'Payment')]"));

                            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", payBtn);
                            try {
                                Thread.sleep(300); // brief pause after scroll
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                throw new RuntimeException("Interrupted while waiting for UI element scroll", e);
                            }

                            try {
                                payBtn.click();
                            } catch (org.openqa.selenium.ElementClickInterceptedException e) {
                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", payBtn); // fallback click
                            }

                            ReportManager.setTest(rootTest); // Reset to root context
                            return new SinglePaymentPage(driver);
                        }
                    }
                    index++;
                }
            } else {
                index++;
            }
        }

        MasterLogger.step(Layer.UI, "Invoice RefNo NOT found: " + invoiceRefNo, () -> {});
        throw new IllegalStateException("Invoice RefNo not found: " + invoiceRefNo);
    }
}
