package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Inventory.MaterialInwardDataFactory;
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Purchase.FieldValidator.PaymentValidator;
import com.Vcidex.StoryboardSystems.Purchase.FieldValidator.ReceiveInvoiceValidator;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Payment.SinglePaymentPage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.TestBase;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PaymentDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseInvoiceDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseOrderDataFactory;
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

public class PurchaseFlowTcasesTest extends TestBase {

    PurchaseOrderDataFactory factory = new PurchaseOrderDataFactory(new ApiMasterDataProvider(baseUrl, token));

    @DataProvider(name = "purchaseTwayData")
    public Object[][] provideTwayCombinations() {
        return new Object[][] {
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.PI_PO, "INR") },
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.DIRECT_PO, "INR") },
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.DIRECT_INVOICE, "GBP") },
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.PURCHASE_AGREEMENT, "GBP") },
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.DIRECT_PO, "GBP") },
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.PI_PO, "GBP") },
                { new PurchaseTestInput(ProductType.PHYSICAL, EntryType.PURCHASE_AGREEMENT, "INR") },
                { new PurchaseTestInput(ProductType.SERVICE, EntryType.DIRECT_INVOICE, "INR") }
        };
    }

    @Test(dataProvider = "purchaseTwayData", retryAnalyzer = com.Vcidex.StoryboardSystems.Utils.Logger.RetryAnalyzer.class)
    public void testPurchaseFlow(PurchaseTestInput input) {
        ExtentTest node = ReportManager.createTest("T-way Test: " + input);
        WebDriver driver = ThreadSafeDriverManager.getDriver();

        PurchaseOrderData poData = factory.generateDataFor(input);

        // Step 1: Direct PO creation
        DirectPO poPage = new DirectPO(driver);
        poPage.openDirectPOModal(node);
        poPage.fillForm(poData, node);
        poPage.submitDirectPO(node);

        Assert.assertNotNull(poData.getPoRefNo(), "PO Reference Number should not be null");
        Assert.assertFalse(poData.getPoRefNo().trim().isEmpty(), "PO Reference Number should not be empty");

        // Step 2: GRN only for physical products
        if (input.getProductType() == ProductType.PHYSICAL) {
            MaterialInwardData inwardData = MaterialInwardDataFactory.createFromPO(poData);
            MaterialInwardPage inwardPage = new MaterialInwardPage(driver);
            inwardPage.createInwardEntry(inwardData, node);

            Assert.assertEquals(inwardData.getLineItems().size(), poData.getLineItems().size(),
                    "Mismatch in line item count between PO and Inward");
        }

        // Step 3: Invoice creation
        PurchaseInvoiceData invoiceData = null;
        ReceiveInvoicePage invoicePage = new ReceiveInvoicePage(driver);

        if (input.getEntryType() != EntryType.DIRECT_INVOICE) {
            invoiceData = PurchaseInvoiceDataFactory.createFromPO(poData);
            invoicePage.fillInvoiceForm(invoiceData, node);
            invoicePage.submitInvoice(node);

            ValidationLogger.assertEquals("Vendor Name", poData.getVendorName(), invoiceData.getVendorName(), node);
            ValidationLogger.assertEquals("Currency", poData.getCurrency(), invoiceData.getCurrency(), node);
            ValidationLogger.assertEquals("Grand Total", poData.getGrandTotal(), invoiceData.getGrandTotal(), node);
            ValidationLogger.assertTrue("Invoice Ref No should not be null", invoiceData.getInvoiceRefNo() != null, node);
            ReceiveInvoiceValidator.verifyInvoiceAgainstPO(poData, invoicePage, driver);
        }

        // Step 4: Payment
        PaymentData paymentData = null;
        if (invoiceData != null) {
            paymentData = new PaymentDataFactory().createFromInvoice(invoiceData);
            SinglePaymentPage paymentPage = new SinglePaymentPage(driver);
            paymentPage.makePayment(paymentData, node);

            ValidationLogger.assertEquals("Invoice Ref in Payment", paymentData.getInvoiceRefNo(), invoiceData.getInvoiceRefNo(), node);
            ValidationLogger.assertEquals("Payment Amount", paymentData.getPaymentAmount(), invoiceData.getGrandTotal(), node);
            PaymentValidator.compareWithInvoice(paymentData, invoicePage, node);
        }

        // Final Log Summary
        node.info("✅ Flow Completed: " + input);
        node.info("🔖 PO Ref: " + poData.getPoRefNo());
        if (invoiceData != null)
            node.info("📄 Invoice Ref: " + invoiceData.getInvoiceRefNo());

        ValidationLogger.assertAll("End-to-End Purchase Flow");
    }
}