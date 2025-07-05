package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Inventory.Navigation.MaterialInwardNavigator;
import com.Vcidex.StoryboardSystems.Inventory.MaterialInwardDataFactory;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Purchase.FieldValidator.PaymentValidator;
import com.Vcidex.StoryboardSystems.Purchase.FieldValidator.ReceiveInvoiceValidator;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.DirectPONavigator;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.PaymentNavigator;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.ReceiveInvoiceNavigator;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.TestBase;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PaymentDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseInvoiceDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput;
import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import com.Vcidex.StoryboardSystems.Purchase.Model.EntryType;

import java.time.Duration;

public class PurchaseFlowTcasesTest extends TestBase {

    @DataProvider(name = "purchaseTwayData")
    public Object[][] provideTwayCombinations() {
        return new Object[][] {
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.DIRECT_PO, "INR") },
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.DIRECT_INVOICE, "GBP") },
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.DIRECT_PO, "GBP") },
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.DIRECT_INVOICE, "INR") }
        };
    }

    @Test(dataProvider = "purchaseTwayData")
    public void testPurchaseFlow(PurchaseTestInput input) {
        long startTime = System.currentTimeMillis(); // 🕒 Start clock

        ExtentTest node = ReportManager.createTest("T-way Test: " + input);
        WebDriver driver = ThreadSafeDriverManager.getDriver();

        // ── Test Data Generation ──
        long t0 = System.currentTimeMillis();
        PurchaseOrderData poData = factory.generateDataFor(input);
        Assert.assertNotNull(poData, "PO data must not be null");
        Assert.assertFalse(poData.getLineItems().isEmpty(), "PO must have line items");
        node.info("🧪 Test data generated in " + (System.currentTimeMillis() - t0) + " ms");

        // ── Step 1: Direct PO ──
        t0 = System.currentTimeMillis();
        DirectPONavigator poNav = new DirectPONavigator(driver, new NavigationManager(driver), node);
        DirectPO poPage = poNav.openDirectPO();
        poPage.fillForm(poData, node);
        poPage.submitDirectPO(node);
        long t1 = System.currentTimeMillis();
        node.info("📦 [PO Created in " + (t1 - t0) + " ms]");

        // ── Step 2: Material Inward ──
        if (poData.getProductType() == ProductType.PHYSICAL) {
            t0 = System.currentTimeMillis();
            MaterialInwardNavigator inwardNav = new MaterialInwardNavigator(driver, new NavigationManager(driver), node);
            MaterialInwardPage inwardPage = inwardNav.openAddInwardModal();
            inwardPage.createInwardEntry(MaterialInwardDataFactory.createFromPO(poData), node);
            t1 = System.currentTimeMillis();
            node.info("📥 [Inward Entry Done in " + (t1 - t0) + " ms]");
        }

        // ── Step 3: Invoice ──
        PurchaseInvoiceData invoiceData = null;
        if (input.getEntryType() != EntryType.DIRECT_INVOICE) {
            t0 = System.currentTimeMillis();
            ReceiveInvoiceNavigator invoiceNav = new ReceiveInvoiceNavigator(driver, new NavigationManager(driver), node);
            ReceiveInvoicePage invoicePage = invoiceNav.openReceiveInvoicePage(poData.getPoRefNo(),
                    poData.getProductType() == ProductType.SERVICE);
            invoiceData = PurchaseInvoiceDataFactory.createFromPO(poData);
            invoicePage.fillInvoiceForm(invoiceData, node);
            invoicePage.submitInvoice(node);
            t1 = System.currentTimeMillis();
            node.info("🧾 [Invoice Created in " + (t1 - t0) + " ms]");
        }

        // ── Step 4: Payment ──
        if (invoiceData != null) {
            t0 = System.currentTimeMillis();
            PaymentData paymentData = new PaymentDataFactory().createFromInvoice(invoiceData);
            PaymentNavigator payNav = new PaymentNavigator(driver, new NavigationManager(driver), node);
            payNav.openSinglePayment(invoiceData.getVendorName(), invoiceData.getInvoiceRefNo())
                    .makePayment(paymentData, node);
            t1 = System.currentTimeMillis();
            node.info("💰 [Payment Done in " + (t1 - t0) + " ms]");
        }


        // ── Final Log ──
        long totalTime = System.currentTimeMillis() - startTime;
        node.info("⏱️ Total test time: " + totalTime + " ms");
        node.info("✅ Flow Completed for: " + input);
        ValidationLogger.assertAll("End-to-End Purchase Flow");
    }
}