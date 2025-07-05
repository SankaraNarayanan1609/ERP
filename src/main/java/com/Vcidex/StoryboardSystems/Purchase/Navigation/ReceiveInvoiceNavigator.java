// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Navigation/ReceiveInvoiceNavigator.java
package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ReceiveInvoiceNavigator {
    private final WebDriver         driver;
    private final NavigationManager nav;
    private final ExtentTest        rootTest;

    public ReceiveInvoiceNavigator(
            WebDriver driver,
            NavigationManager nav,
            ExtentTest rootTest
    ) {
        this.driver   = driver;
        this.nav      = nav;
        this.rootTest = rootTest;
    }

    /**
     * Navigate → open & return the ReceiveInvoicePage.
     * @param orderRefNo the PO reference to select
     * @param isService  true to switch to Service tab, false for Products
     */
    public ReceiveInvoicePage openReceiveInvoicePage(String orderRefNo, boolean isService) {
        ExtentTest node = rootTest.createNode("📄 Open Receive Invoice Page");
        ReportManager.setTest(node);

        // 1) Navigate to Invoice Summary via menu
        MasterLogger.step(Layer.UI, "Navigate ▶ Purchase → Payable → Invoice", () -> {
            nav.goTo("Purchase", "Payable", "Invoice");
            return null;
        });

        // 2) Validate “Invoice Summary” title
        MasterLogger.step(Layer.VALIDATION, "Validate page title is 'Invoice Summary'", () -> {
            nav.waitUntilVisible(
                    By.xpath("//h3[contains(@class,'card-title') and normalize-space()='Invoice Summary']"),
                    "Waiting for Invoice Summary title"
            );
            return null;
        });

        // 3) Click '+ Receive Invoice'
        MasterLogger.step(Layer.UI, "Click '+ Receive Invoice' button", () -> {
            nav.waitForOverlayClear(); // 'waitForOverlayClear()' has protected access in 'com.Vcidex.StoryboardSystems.Common.BasePage'
            By recvBtn = By.xpath("//button[contains(@title,'Receive Invoice')]");
            try {
                nav.waitUntilClickable(recvBtn).click();
            } catch (Exception e) {
                WebElement elm = driver.findElement(recvBtn);
                ((JavascriptExecutor)driver).executeScript(
                        "arguments[0].scrollIntoView(true); arguments[0].click();", elm
                );
            }
            nav.waitForOverlayClear(); // 'waitForOverlayClear()' has protected access in 'com.Vcidex.StoryboardSystems.Common.BasePage'
            return null;
        });

        // 4) Validate “Receive Invoice” heading
        MasterLogger.step(Layer.VALIDATION, "Validate title 'Receive Invoice'", () -> {
            nav.waitUntilVisible(
                    By.xpath("//h2[contains(@class,'card-title') and normalize-space()='Receive Invoice']"),
                    "Waiting for Receive Invoice title"
            );
            return null;
        });

        // 5) Switch tab based on product type
        MasterLogger.step(Layer.UI, "Switch to " + (isService ? "Service" : "Products") + " tab", () -> {
            String paneId = isService ? "kt_tab_pane_3" : "kt_tab_pane_2";
            By tabSelector = By.cssSelector("ul.nav-tabs a[data-bs-toggle='tab'][href='#" + paneId + "']");

            // explicit, short wait + click
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.elementToBeClickable(tabSelector))
                    .click();

            // wait for any tiny overlay
            new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".overlay, .spinner")));
            return null;
        });


        // 6) Select the PO from the list
        MasterLogger.step(Layer.UI, "Select PO " + orderRefNo, () -> {
            // Wait for section header
            nav.waitUntilVisible(
                    By.xpath("//h4[contains(text(),'Pending Invoices')]"),
                    "Waiting for Pending Invoices header"
            );
            // Then for table rows
            nav.waitUntilVisible(
                    By.cssSelector("table#purchaseOrderList tbody tr"),
                    "Waiting for invoice PO rows"
            );
            String xpath = String.format(
                    "//table[@id='purchaseOrderList']//tr[td[normalize-space()='%s']]//button[normalize-space()='Select']",
                    orderRefNo
            );
            By selectBtn = By.xpath(xpath);
            try {
                nav.waitUntilClickable(selectBtn).click();
            } catch (Exception e) {
                WebElement elm = driver.findElement(selectBtn);
                ((JavascriptExecutor)driver).executeScript(
                        "arguments[0].scrollIntoView(true); arguments[0].click();", elm
                );
            }
            return null;
        });

        // Reset to root test
        ReportManager.setTest(rootTest);
        return new ReceiveInvoicePage(driver);
    }
}