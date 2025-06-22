// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Navigation/ReceiveInvoiceNavigator.java
package com.Vcidex.StoryboardSystems.Purchase.Navigation;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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
     * Navigate → open & return the ReceiveInvoicePage
     */
    public ReceiveInvoicePage openReceiveInvoicePage(String orderRefNo) {
        ExtentTest node = rootTest.createNode("📄 Open Receive Invoice Page");
        ReportManager.setTest(node);

        MasterLogger.step(Layer.UI, "Navigate to Invoice Summary page", () -> {
            nav.goTo("Purchase", "Payable", "Invoice");
            return null;
        });

        MasterLogger.step(Layer.VALIDATION, "Validate page title is 'Invoice Summary'", () -> {
            By invoiceSummaryTitle = By.xpath(
                    "//h3[contains(@class,'card-title') and normalize-space()='Invoice Summary']"
            );
            nav.waitUntilVisible(invoiceSummaryTitle, "Waiting for Invoice Summary title");
            return null;
        });

        MasterLogger.step(Layer.UI, "Click '+ Receive Invoice' button", () -> {
            // clear any lingering overlay
            nav.waitForOverlayClear();
            By receiveInvoiceBtn = By.xpath("//button[contains(@title,'Receive Invoice')]");
            nav.waitForOverlayClear();
            nav.waitUntilClickable(receiveInvoiceBtn).click();
            // wait again in case a new overlay appears
            nav.waitForOverlayClear();
            return null;
        });

        MasterLogger.step(Layer.VALIDATION, "Validate page title is 'Receive Invoice'", () -> {
            By receiveInvoiceTitle = By.xpath(
                    "//h2[contains(@class,'card-title') and normalize-space()='Receive Invoice']"
            );
            nav.waitUntilVisible(receiveInvoiceTitle, "Waiting for Receive Invoice title");
            return null;
        });

        MasterLogger.step(Layer.UI, "Click 'Select' button for Order Ref No: " + orderRefNo, () -> {
            // wait for the invoice table to render
            nav.waitUntilVisible(
                    By.cssSelector("table#purchaseOrderList tbody tr"),
                    "Waiting for Receive-Invoice PO list to render"
            );

            String xpath = String.format(
                    "//table[@id='purchaseOrderList']//tr[td[normalize-space()='%s']]" +
                            "//button[normalize-space()='Select']",
                    orderRefNo
            );
            By selectBtnForPO = By.xpath(xpath);
            nav.waitUntilClickable(selectBtnForPO).click();
            return null;
        });

        ReportManager.setTest(rootTest);
        return new ReceiveInvoicePage(driver);
    }
}