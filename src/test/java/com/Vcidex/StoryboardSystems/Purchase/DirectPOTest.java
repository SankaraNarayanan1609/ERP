package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Inventory.Navigation.MaterialInwardNavigator;
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Listeners.LogoutDetector;
import com.Vcidex.StoryboardSystems.LoginManager;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.DirectPONavigator;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.PaymentNavigator;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.ReceiveInvoiceNavigator;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Payment.SinglePaymentPage;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.TestBase;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PaymentDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseFlowFactory;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseInvoiceDataFactory;
import com.Vcidex.StoryboardSystems.Utils.DebugUtils;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.Vcidex.StoryboardSystems.Utils.MasterDataLoader;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.*;

@Listeners({
        LogoutDetector.class,
        com.Vcidex.StoryboardSystems.Listeners.TestLifecycleListener.class
})
public class DirectPOTest extends TestBase {
    private static final String BASE_URL = "https://erplite.storyboarderp.com";

    private NavigationManager nav;
    private DirectPONavigator poNav;
    private MaterialInwardNavigator inwardNav;
    private PurchaseInvoiceDataFactory invoiceFactory;
    private String token;

    private ExtentTest rootTest;

    @BeforeMethod(alwaysRun = true)
    @Parameters({ "appName", "companyCode", "userId" })
    public void setUp(
            @Optional("StoryboardSystems") String appName,
            @Optional("vcidex")            String companyCode,
            @Optional("vcx288")            String userId
    ) {
        rootTest  = ReportManager.createTest("DirectPO + Material Inward Flow", "E2E Purchase + Inward");
        nav       = new NavigationManager(driver);
        poNav     = new DirectPONavigator(driver, nav, rootTest);
        inwardNav = new MaterialInwardNavigator(driver, nav, rootTest);

        ExtentTest loginNode = rootTest.createNode("🔑 Login");
        new LoginManager(driver, loginNode).loginViaUi(appName, companyCode, userId);
        DebugUtils.waitForAngular(driver);

        token = (String)((JavascriptExecutor)driver)
                .executeScript("return window.localStorage.getItem('token');");

        if (token == null || token.isEmpty())
            throw new IllegalStateException("No token in localStorage after login");

        TestBase.initDataFactory(token);
        new MasterDataLoader(BASE_URL, token).loadAndValidate();

        ApiMasterDataProvider provider = new ApiMasterDataProvider(BASE_URL, token);
        invoiceFactory = new PurchaseInvoiceDataFactory(provider);
    }

    @Test(description = "Create Direct PO → then Material Inward → then Invoice → then Payment")
    public void testCreateDirectPOThenMaterialInward() {
        ApiMasterDataProvider provider = new ApiMasterDataProvider(BASE_URL, token);
        PurchaseFlowFactory flowFactory = new PurchaseFlowFactory(provider);

        // ── Step 1: Create PO ──
        ExtentTest poDataNode = rootTest.createNode("🛠 PO Data");
        PurchaseOrderData poData = flowFactory.createDirectPO(false);
        poDataNode.pass("✅ Generated PO Ref = " + poData.getPoRefNo());

        DirectPO poPage = poNav.openDirectPO();
        ExtentTest poFillNode = rootTest.createNode("📝 Fill Direct PO");
        poPage.fillForm(poData, poFillNode);

        ExtentTest poSubmitNode = rootTest.createNode("🚀 Submit Direct PO");
        poPage.submitDirectPO(poSubmitNode);

        // ── Step 2: Inward ──
        MaterialInwardData miData = flowFactory.createInwardFromPO(poData, poPage.getLineItems());
        MaterialInwardPage miPage = inwardNav.openAddInwardModal();
        inwardNav.selectPurchaseOrder(miPage, poData.getPoRefNo());

        ExtentTest miDataNode = rootTest.createNode("🛠 Inward Data");
        miDataNode.pass("✅ DC No = " + miData.getDcNo());

        ExtentTest miFillNode = rootTest.createNode("📝 Fill Material Inward");
        miPage.fillInwardDetails(miData, miFillNode);

        ExtentTest verifyNode = rootTest.createNode("🔎 Verify new inward in summary");
        miPage.clickBack(verifyNode);
        miPage.assertInwardListed(miData.getDcNo(), verifyNode);

        // ── Step 3: Invoice ──
        ReceiveInvoiceNavigator invoiceNav = new ReceiveInvoiceNavigator(driver, nav, rootTest);
        ReceiveInvoicePage invoicePage = invoiceNav.openReceiveInvoicePage(poData.getPoRefNo());

        PurchaseInvoiceData invoiceData = flowFactory.createInvoiceFromPO(poData);
        ExtentTest invoiceDataNode = rootTest.createNode("🛠 Invoice Data");
        invoiceDataNode.pass("✅ Invoice Ref = " + invoiceData.getInvoiceRefNo());

        ExtentTest invoiceFillNode = rootTest.createNode("📝 Fill Receive Invoice");
        invoicePage.fillInvoiceForm(invoiceData, invoiceFillNode);

        ExtentTest invoiceSubmitNode = rootTest.createNode("🚀 Submit Invoice");
        invoicePage.submitInvoice(invoiceSubmitNode);

        // ── Step 4: Payment ──
        PaymentDataFactory paymentFactory = new PaymentDataFactory();
        PaymentData paymentData = paymentFactory.createFromInvoice(invoiceData);

        PaymentNavigator paymentNav = new PaymentNavigator(driver, nav, rootTest);
        SinglePaymentPage paymentPage = paymentNav.openSinglePayment(poData.getVendorName(), invoiceData.getInvoiceRefNo());

        ExtentTest paymentFillNode = rootTest.createNode("📝 Fill Payment Form");
        paymentPage.fillPaymentForm(paymentData, paymentFillNode); // Expected 5 arguments but found 2

        ExtentTest paymentSubmitNode = rootTest.createNode("🚀 Submit Payment");
        paymentPage.submitPayment(paymentSubmitNode);
    }
}