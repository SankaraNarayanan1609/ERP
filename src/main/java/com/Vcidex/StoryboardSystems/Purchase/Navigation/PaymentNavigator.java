package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Payment.SinglePaymentPage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
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
        ExtentTest node = rootTest.createNode("\uD83D\uDD28 Navigate to Single Payment");
        ReportManager.setTest(node);

        MasterLogger.step(Layer.UI, "Navigate to Payment page", () -> {
            nav.goTo("Purchase", "Payable", "Payment");
            return null;
        });

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        // Assert you're in Payment Summary
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h3[contains(text(),'Payment Summary')]")));

        // Click Add Payment
        driver.findElement(By.xpath("//button[contains(text(),'Add Payment')]"))
                .click();

        // Assert Add Payment screen is loaded
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(text(),'Add Payment')]")));

        // Locate vendor column and expand
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//th[contains(text(),'Vendor')]")));

        List<WebElement> vendorRows = driver.findElements(By.xpath("//td[i[contains(@class,'bi-chevron-right')]]"));
        for (WebElement row : vendorRows) {
            row.click(); // Expand

            // Smart wait: wait for invoice rows to be visible after expanding the vendor
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tr[contains(@class,'text-nowrap')]//td[3]")
            ));

            List<WebElement> invoiceRefs = driver.findElements(By.xpath("//tr[contains(@class,'text-nowrap')]//td[3]"));
            for (WebElement cell : invoiceRefs) {
                if (cell.getText().trim().equals(invoiceRefNo)) {
                    // Click associated payment button in that row
                    WebElement rowElement = cell.findElement(By.xpath("ancestor::tr"));
                    WebElement payButton = rowElement.findElement(By.xpath(".//button[contains(text(),'Payment')]"));
                    payButton.click();
                    ReportManager.setTest(rootTest);
                    return new SinglePaymentPage(driver);
                }
            }
        }

        throw new IllegalStateException("Invoice RefNo not found: " + invoiceRefNo);
    }
}
