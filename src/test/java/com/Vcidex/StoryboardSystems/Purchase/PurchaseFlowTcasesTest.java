package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Inventory.Navigation.MaterialInwardNavigator;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Inventory.MaterialInwardDataFactory;
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
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
import com.Vcidex.StoryboardSystems.Purchase.Pages.Payment.SinglePaymentPage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.TestBase;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PaymentDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseInvoiceDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DebugUtils;
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

    // Fetch the apiBase and authToken using the updated ConfigManager
    String apiBase = ConfigManager.getApiBase("test", "StoryboardSystems"); // Specify correct environment and app name
    String authToken = ConfigManager.getAuthConfig("test").optString("clientId"); // Or any relevant method to fetch the token
    PurchaseOrderDataFactory factory = new PurchaseOrderDataFactory(new ApiMasterDataProvider(apiBase, authToken));

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

        try {
            Thread.sleep(10000); // 🔍 Pauses 10 seconds to observe browser
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PurchaseOrderData poData = factory.generateDataFor(input);
        System.out.println("🔧 DEBUG: Generated PO Data: " + poData);
        Assert.assertNotNull(poData, "❌ PO data is null");

        System.out.println("🔧 DEBUG: Line Items: " + poData.getLineItems());
        Assert.assertNotNull(poData.getLineItems(), "❌ PO line items are null");
        Assert.assertFalse(poData.getLineItems().isEmpty(), "❌ PO has no line items");

        System.out.println("🔧 DEBUG: PO Vendor: " + poData.getVendorName());
        System.out.println("🔧 DEBUG: PO Currency: " + poData.getCurrency());


        ReceiveInvoicePage invoicePage = null; // ✅ Declare early for reuse in Payment

        // Step 1: Direct PO Creation
        DirectPONavigator poNav = new DirectPONavigator(driver, new NavigationManager(driver), node);
        DirectPO poPage = poNav.openDirectPO();
        DebugUtils.waitForAngular(driver); // ✅ Wait here just like DirectPOTest
        poPage.fillForm(poData, node);

        // ✅ Add this right after fillForm()
        System.out.println("🧪 After fillForm() - Line Items in poData: " + poData.getLineItems().size());
        poData.getLineItems().forEach(item ->
                System.out.println("   -> " + item.getProductName() + " | " + item.getProductCode()));

        poPage.submitDirectPO(node);

        Assert.assertNotNull(poData.getPoRefNo(), "PO Reference Number should not be null");
        Assert.assertFalse(poData.getPoRefNo().trim().isEmpty(), "PO Reference Number should not be empty");

        // Step 2: Inward for Physical Products
        if (input.getProductType() == ProductType.PHYSICAL) {
            MaterialInwardNavigator inwardNav = new MaterialInwardNavigator(driver, new NavigationManager(driver), node);
            MaterialInwardPage inwardPage = inwardNav.openAddInwardModal();
            DebugUtils.waitForAngular(driver);  // ✅ Ensure UI is stable

            MaterialInwardData inwardData = MaterialInwardDataFactory.createFromPO(poData);
            inwardPage.createInwardEntry(inwardData, node);

            Assert.assertEquals(
                    inwardData.getLineItems().size(),
                    poData.getLineItems().size(),
                    "Mismatch in line item count between PO and Inward"
            );
        }

        // Step 3: Invoice Creation (Skip only if Direct Invoice)
        PurchaseInvoiceData invoiceData = null;
        if (input.getEntryType() != EntryType.DIRECT_INVOICE) {
            ReceiveInvoiceNavigator invoiceNav = new ReceiveInvoiceNavigator(driver, new NavigationManager(driver), node);
            invoicePage = invoiceNav.openReceiveInvoicePage(poData.getPoRefNo());
            DebugUtils.waitForAngular(driver);  // ✅ Add this
            invoiceData = PurchaseInvoiceDataFactory.createFromPO(poData);
            invoicePage.fillInvoiceForm(invoiceData, node); // ✅ after data is available

            invoicePage.submitInvoice(node);

            ValidationLogger.assertEquals("Vendor Name", poData.getVendorName(), invoiceData.getVendorName(), node);
            ValidationLogger.assertEquals("Currency", poData.getCurrency(), invoiceData.getCurrency(), node);
            ValidationLogger.assertEquals("Grand Total", poData.getGrandTotal(), invoiceData.getGrandTotal(), node);
            ValidationLogger.assertTrue("Invoice Ref No should not be null", invoiceData.getInvoiceRefNo() != null, node);

            ReceiveInvoiceValidator.verifyInvoiceAgainstPO(poData, invoicePage, driver);
        }

        // Step 4: Payment (only if invoice exists)
        if (invoiceData != null) {
            PaymentData paymentData = new PaymentDataFactory().createFromInvoice(invoiceData);
            PaymentNavigator payNav = new PaymentNavigator(driver, new NavigationManager(driver), node);
            SinglePaymentPage paymentPage = payNav.openSinglePayment(invoiceData.getVendorName(), invoiceData.getInvoiceRefNo());
            DebugUtils.waitForAngular(driver);  // ✅ Add this
            paymentPage.makePayment(paymentData, node);

            ValidationLogger.assertEquals("Invoice Ref in Payment", paymentData.getInvoiceRefNo(), invoiceData.getInvoiceRefNo(), node);
            ValidationLogger.assertEquals("Payment Amount", paymentData.getPaymentAmount(), invoiceData.getGrandTotal(), node);

            PaymentValidator.compareWithInvoice(paymentData, invoicePage, node); // ✅ invoicePage now initialized
        }

        // Step 5: Summary Logs
        node.info("✅ Flow Completed: " + input);
        node.info("🔖 PO Ref: " + poData.getPoRefNo());
        if (invoiceData != null) {
            node.info("📄 Invoice Ref: " + invoiceData.getInvoiceRefNo());
        }

        ValidationLogger.assertAll("End-to-End Purchase Flow");
    }

}