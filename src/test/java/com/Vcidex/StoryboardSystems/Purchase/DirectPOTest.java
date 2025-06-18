/**
 * DirectPOTest is the main test class that automates the end-to-end purchase flow
 * in the SBS ERP system.
 *
 * Covered flow:
 *   1. Create a Direct Purchase Order (PO)
 *   2. Perform Material Inward (Goods Receipt)
 *   3. Generate Purchase Invoice
 *   4. Execute Payment
 *
 * This test validates the full cycle from ordering to payment using:
 *   - Page Object Model (POM) design pattern
 *   - Factory-based test data generation (realistic + dynamic)
 *   - ExtentReports for structured test logging
 *   - Selenium WebDriver for browser interaction
 *
 *   - This test combines many parts of the framework (navigation, data, page objects, and reporting)
 *   - You’ll see @BeforeMethod and @Test annotations from TestNG (a test execution framework)
 *   - All data is generated dynamically from APIs and Java Faker
 */

package com.Vcidex.StoryboardSystems.Purchase; // This file is part of the Purchase module test folder

// ─────────────────────────────────────────────────────────────────────────────
// SECTION: IMPORTS
// These are all the external and internal libraries and classes needed
// ─────────────────────────────────────────────────────────────────────────────

// 📦 Core Framework Classes: Navigation and Master Login
import com.Vcidex.StoryboardSystems.Common.NavigationManager;  // Handles common menu & page routing
import com.Vcidex.StoryboardSystems.LoginManager;              // Handles login using UI

// 📦 Purchase Module Navigation and Pages
import com.Vcidex.StoryboardSystems.Purchase.Navigation.DirectPONavigator;     // Navigates to Direct PO screen
import com.Vcidex.StoryboardSystems.Purchase.Navigation.ReceiveInvoiceNavigator; // Navigates to Invoice screen
import com.Vcidex.StoryboardSystems.Purchase.Navigation.PaymentNavigator;      // Navigates to Payment screen
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;    // Page object for Direct PO form
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage; // Page object for Invoice
import com.Vcidex.StoryboardSystems.Purchase.Pages.Payment.SinglePaymentPage;  // Page object for Payment

// 📦 Inventory Module Dependencies
import com.Vcidex.StoryboardSystems.Inventory.Navigation.MaterialInwardNavigator;  // Navigates to Inward screen
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;     // Page object for Inward
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;             // Inward data model

// 📦 Test Data Models (POJO = Plain Old Java Object)
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;      // PO data (fields like vendor, currency)
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;    // Invoice data model
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;            // Payment data model

// 🏭 Test Data Factories (dynamic data creators)
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;       // Fetches master data from API
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseFlowFactory;        // One-stop factory for PO-Invoice-Inward
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseInvoiceDataFactory; // Factory for Invoice test data
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PaymentDataFactory;         // Factory for Payment test data

// 🧪 Test Framework Base & Reporting
import com.Vcidex.StoryboardSystems.TestBase;             // Parent base class for test setup and driver
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager; // ExtentReport logger

// 🛠 Master Data & Config
import com.Vcidex.StoryboardSystems.Utils.MasterDataLoader;        // Loads master data from API
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;    // Reads config.json for tokens, URLs

// 🧪 Angular Wait Helper
import com.Vcidex.StoryboardSystems.Utils.DebugUtils;              // Waits for Angular JS to stabilize

// 📦 External Libraries
import com.aventstack.extentreports.ExtentTest;      // Reporting library for test steps
import org.json.JSONObject;                          // JSON parsing from config
import org.openqa.selenium.JavascriptExecutor;       // Executes JavaScript in browser
import org.testng.annotations.*;                     // TestNG annotations like @BeforeMethod, @Test

// 📢 Listener Hooks for logout or test failure
import com.Vcidex.StoryboardSystems.Listeners.LogoutDetector;

/**
 * ─────────────────────────────────────────────────────────────────────────────
 * - Each screen in the ERP (PO, Invoice, Inward, Payment) has its own "Page" class
 * - Navigators help us reach the page
 * - Factories generate fake data (realistic and dynamic)
 * - This test combines them into one end-to-end test case
 *
 * 🌐 Framework Layers Used Here:
 * - Page Layer: DirectPO, ReceiveInvoicePage, etc.
 * - Navigator Layer: Helps click through left menus
 * - Data Layer: PurchaseOrderData, generated by factories
 * - Base Layer: TestBase and LoginManager
 * - Report Layer: ExtentTest via ReportManager
 *
 * 📋 Always follow this order in test flows:
 *  1. Login
 *  2. Navigation
 *  3. Data generation
 *  4. Page actions (fill form, submit)
 *  5. Validation (assertions)
 * ─────────────────────────────────────────────────────────────────────────────
 */

@Listeners({
        LogoutDetector.class, // Handles logout errors mid-test
        com.Vcidex.StoryboardSystems.Listeners.TestLifecycleListener.class // Handles screenshot/reporting
})
public class DirectPOTest extends TestBase {

    // ─── Reusable Navigators for Module Pages ───────────────────────────────────────
    private NavigationManager nav;
    private DirectPONavigator poNav;
    private MaterialInwardNavigator inwardNav;
    private ReceiveInvoiceNavigator invoiceNav;
    private PaymentNavigator paymentNav;

    // ─── Data Factories for Creating Test Data ──────────────────────────────────────
    private PurchaseFlowFactory flowFactory;
    private PurchaseInvoiceDataFactory invoiceFactory;

    // ─── Runtime Variables ──────────────────────────────────────────────────────────
    private String baseUrl;
    private String token;
    private ExtentTest rootTest;

    /**
     * Sets up the test environment before each test method.
     * - Reads credentials
     * - Logs in via UI
     * - Extracts token
     * - Loads master data from API
     * - Initializes data factories
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({ "appName", "companyCode", "userId" })
    public void setUp(
            @Optional String paramAppName,
            @Optional String paramCompanyCode,
            @Optional String paramUserId
    ) {
        // Use passed parameters or fallback to TestBase defaults
        if (paramAppName != null)     this.appName     = paramAppName;
        if (paramCompanyCode != null) this.companyCode = paramCompanyCode;
        if (paramUserId != null)      this.userId      = paramUserId;

        // Create root ExtentTest node for reporting
        rootTest = ReportManager.createTest("DirectPO + Material Inward Flow", "E2E Purchase + Inward");

        // Initialize Navigation Managers (wrapper over left menu actions)
        nav        = new NavigationManager(driver);
        poNav      = new DirectPONavigator(driver, nav, rootTest);
        inwardNav  = new MaterialInwardNavigator(driver, nav, rootTest);
        invoiceNav = new ReceiveInvoiceNavigator(driver, nav, rootTest);
        paymentNav = new PaymentNavigator(driver, nav, rootTest);

        // Perform login via UI using LoginManager
        new LoginManager(driver, rootTest.createNode("🔑 Login"))
                .loginViaUi(appName, companyCode, userId);

        // Wait until Angular application is stable
        DebugUtils.waitForAngular(driver);

        // Read token from browser's localStorage
        token = (String)((JavascriptExecutor)driver)
                .executeScript("return window.localStorage.getItem('token');");

        if (token == null || token.isEmpty())
            throw new IllegalStateException("No token in localStorage after login");

        // Fetch app config from config.json
        JSONObject appConfig = ConfigManager.getAppConfig(System.getProperty("env", "test"), appName);
        baseUrl = appConfig.getString("apiBase");

        // Setup factory classes using token
        TestBase.initDataFactory(token);

        // Load master data and validate API success
        new MasterDataLoader(baseUrl, token).loadAndValidate();

        // Initialize test data factories using live master data
        ApiMasterDataProvider provider = new ApiMasterDataProvider(baseUrl, token);
        invoiceFactory = new PurchaseInvoiceDataFactory(provider);
        flowFactory    = new PurchaseFlowFactory(provider);
    }

    /**
     * This is the main test case:
     * - Step 1: Create Direct PO
     * - Step 2: Create Material Inward from PO
     * - Step 3: Create Invoice from PO
     * - Step 4: Create Payment from Invoice
     */
    @Test(description = "Create Direct PO → then Material Inward → then Invoice → then Payment")
    public void testCreateDirectPOThenMaterialInward() {

        // ─── Step 1: Create Direct PO ─────────────────────────────────────
        PurchaseOrderData poData = flowFactory.createDirectPO(false); // Create randomized PO data
        rootTest.createNode("🛠 PO Data").pass("✅ Generated PO Ref = " + poData.getPoRefNo());

        DirectPO poPage = poNav.openDirectPO(); // Navigate and open PO page
        poPage.fillForm(poData, rootTest.createNode("📝 Fill Direct PO")); // Fill form using data
        poPage.submitDirectPO(rootTest.createNode("🚀 Submit Direct PO")); // Submit the form

        // ─── Step 2: Material Inward (GRN) ───────────────────────────────
        MaterialInwardData miData = flowFactory.createInwardFromPO(poData, poPage.getLineItems());
        MaterialInwardPage miPage = inwardNav.openAddInwardModal(); // Open inward screen

        inwardNav.selectPurchaseOrder(miPage, poData.getPoRefNo()); // Select PO inside inward

        rootTest.createNode("🛠 Inward Data").pass("✅ DC No = " + miData.getDcNo());
        miPage.fillInwardDetails(miData, rootTest.createNode("📝 Fill Material Inward")); // Fill DC, quantity, etc.

        // Calculate number of product-type rows expected (excluding services)
        int expectedRowCount = (int) poData.getLineItems().stream()
                .filter(line  -> line.getProduct() != null)
                .filter(line  -> {
                    String pt = line.getProduct().getProductType();
                    return pt == null || !pt.equalsIgnoreCase("service");
                })
                .count();

        // Validate that expected product lines match what UI shows
        miPage.assertRowCount(expectedRowCount, rootTest.createNode("✅ Assert Product Lines in Inward"));

        // Verify that inward appears in summary page after submission
        miPage.clickBack(rootTest.createNode("🔎 Back to Summary"));
        miPage.assertInwardListed(miData.getDcNo(), rootTest.createNode("🔎 Verify new inward in summary"));

        // ─── Step 3: Create Invoice ───────────────────────────────────────
        ReceiveInvoicePage invoicePage  = invoiceNav.openReceiveInvoicePage(poData.getPoRefNo());
        PurchaseInvoiceData invoiceData = flowFactory.createInvoiceFromPO(poData);

        rootTest.createNode("🛠 Invoice Data").pass("✅ Invoice Ref = " + invoiceData.getInvoiceRefNo());
        invoicePage.fillInvoiceForm(invoiceData, rootTest.createNode("📝 Fill Receive Invoice"));
        invoicePage.submitInvoice(rootTest.createNode("🚀 Submit Invoice"));

        // ─── Step 4: Make Payment ─────────────────────────────────────────
        PaymentData paymentData = new PaymentDataFactory().createFromInvoice(invoiceData);
        SinglePaymentPage paymentPage = paymentNav.openSinglePayment(poData.getVendorName(), invoiceData.getInvoiceRefNo());

        paymentPage.fillPaymentForm(paymentData, rootTest.createNode("📝 Fill Payment Form"));
        paymentPage.submitPayment(rootTest.createNode("🚀 Submit Payment"));
    }
}