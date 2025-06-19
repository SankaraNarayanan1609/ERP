package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Inventory.MaterialInwardDataFactory;
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Payment.SinglePaymentPage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.TestBase;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PaymentDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseInvoiceDataFactory;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.aventstack.extentreports.ExtentTest;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput;
import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import com.Vcidex.StoryboardSystems.Purchase.Model.EntryType;
import org.openqa.selenium.WebDriver;

/**
 * TestNG test class that runs pairwise T-way scenarios
 * based on combinations of ProductType, EntryType (starting point),
 * and Currency. Each row is passed as a PurchaseTestInput object.
 */
public class PurchaseFlowTcasesTest extends TestBase {

    /**
     * Provides 8 optimized 2-way test cases covering all possible
     * pairs of (ProductType, EntryType, Currency).
     */
    @DataProvider(name = "purchaseTwayData")
    public Object[][] provideTwayCombinations() {
        return new Object[][] {
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.PI_PO, "INR") }, // Expected 0 arguments but found 3
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.DIRECT_PO, "INR") }, // Expected 0 arguments but found 3
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.DIRECT_INVOICE, "GBP") }, // Expected 0 arguments but found 3
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.PURCHASE_AGREEMENT, "GBP") }, // Expected 0 arguments but found 3
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.DIRECT_PO, "GBP") }, // Expected 0 arguments but found 3
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.PI_PO, "GBP") },// Expected 0 arguments but found 3
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.PURCHASE_AGREEMENT, "INR") }, // Expected 0 arguments but found 3
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.DIRECT_INVOICE, "INR") } // Expected 0 arguments but found 3
        };
    }

    /**
     * Prints each input scenario to confirm test data execution is working.
     * Replace with actual test execution logic later (DirectPO → GRN → Invoice → Payment).
     */
    @Test(dataProvider = "purchaseScenarios", retryAnalyzer = com.Vcidex.StoryboardSystems.Utils.Logger.RetryAnalyzer.class)
    public void testPurchaseFlow(PurchaseTestInput input) {
        ExtentTest node = ReportManager.createTest("T-way Test: " + input);

        WebDriver driver = ThreadSafeDriverManager.getDriver();

        // Step 1: Generate Purchase Order data
        PurchaseOrderData poData = factory.generateDataFor(input);

        // Step 2: Fill and submit Direct PO
        DirectPO poPage = new DirectPO(driver);
        poPage.openDirectPOModal(node);
        poPage.fillForm(poData, node);
        poPage.submitDirectPO(node);

        // 🔍 Validation: PO Ref No should not be null or empty
        Assert.assertNotNull(poData.getPoRefNo(), "PO Reference Number should not be null");
        Assert.assertFalse(poData.getPoRefNo().trim().isEmpty(), "PO Reference Number should not be empty");

        // Step 3: Inward (only for PHYSICAL products)
        if (input.getProductType() == ProductType.PHYSICAL) {
            MaterialInwardData inwardData = MaterialInwardDataFactory.createFromPO(poData);
            MaterialInwardPage inwardPage = new MaterialInwardPage(driver);
            inwardPage.createInwardEntry(inwardData, node);

            // 🔍 Optional: Validate quantities match
            Assert.assertEquals(inwardData.getLineItems().size(), poData.getLineItems().size(), // Cannot resolve method 'getLineItems' in 'MaterialInwardData'
                    "Mismatch in line item count between PO and Inward");
        }

        // Step 4: Invoice
        PurchaseInvoiceData invoiceData = null;
        if (input.getEntryType() != EntryType.DIRECT_INVOICE) {
            ReceiveInvoicePage invoicePage = new ReceiveInvoicePage(driver);
            invoiceData = PurchaseInvoiceDataFactory.createFromPO(poData); // Cannot resolve symbol 'PurchaseInvoiceDataFactory'
            invoicePage.fillInvoiceForm(invoiceData, node); // Cannot resolve method 'fillInvoice' in 'ReceiveInvoicePage'
            invoicePage.submitInvoice(node);

            // 🔍 Invoice validations
            Assert.assertEquals(invoiceData.getVendorName(), poData.getVendorName(), "Vendor mismatch between PO and Invoice");
            Assert.assertEquals(invoiceData.getCurrency(), poData.getCurrency(), "Currency mismatch");
            Assert.assertEquals(invoiceData.getGrandTotal(), poData.getGrandTotal(), "Grand Total mismatch in Invoice and PO");

            // 🔍 Ref check
            Assert.assertNotNull(invoiceData.getInvoiceRefNo(), "Invoice Reference Number should not be null");
        }

        // Step 5: Payment
        if (invoiceData != null) {
            PaymentData paymentData = new PaymentDataFactory().createFromInvoice(invoiceData);
            SinglePaymentPage paymentPage = new SinglePaymentPage(driver);
            paymentPage.makePayment(paymentData, node);

            // 🔍 Validate payment matches invoice
            Assert.assertEquals(paymentData.getPaymentAmount(), invoiceData.getGrandTotal(), "Payment amount doesn't match Invoice total");
            Assert.assertEquals(paymentData.getInvoiceRefNo(), invoiceData.getInvoiceRefNo(), "Invoice Ref mismatch in Payment");
        }

        // ✅ Log key output
        node.info("✅ Flow Completed: " + input);
        node.info("🔖 PO Ref: " + poData.getPoRefNo());
        if (invoiceData != null)
            node.info("📄 Invoice Ref: " + invoiceData.getInvoiceRefNo());

        ValidationLogger.assertAll("T-way Flow Validation: " + input);
    }
}