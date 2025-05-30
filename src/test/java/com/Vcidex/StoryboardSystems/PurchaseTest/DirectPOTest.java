package com.Vcidex.StoryboardSystems.PurchaseTest;

import com.Vcidex.StoryboardSystems.Listeners.LogoutDetector;
import com.Vcidex.StoryboardSystems.LoginManager;
import com.Vcidex.StoryboardSystems.Common.Navigation.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.Utils.DebugUtils;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.Vcidex.StoryboardSystems.TestBase;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.POJO.MasterData;

@Listeners(LogoutDetector.class)
public class DirectPOTest extends TestBase {
    private static final String BASE_URL = "https://erplite.storyboarderp.com"; // adjust as needed

    private NavigationManager nav;
    private DirectPO directPOPage;
    private ExtentTest rootTest;
    private MasterData allMasters;

    private static final int WAIT_TIMEOUT = Integer.parseInt(System.getProperty("test.wait", "12"));

    @BeforeClass
    public void beforeClass() {
        nav = new NavigationManager(driver);
    }

    @BeforeMethod
    @Parameters({ "appName", "companyCode", "userId" })
    public void setupTest(
            @Optional("StoryboardSystems") String appName,
            @Optional("vcidex")            String companyCode,
            @Optional("vcx288")            String userId
    ) {
        try {
            System.out.println("[DEBUG] === setupTest START ===");

            rootTest = ExtentTestManager.createTest("DirectPO Creation & Submit", "Purchase");
            ExtentTest loginNode = ExtentTestManager.createNode("🔑 Login");

            new LoginManager(driver, loginNode).loginViaUi(appName, companyCode, userId);

            DebugUtils.waitForAngular(driver);

            String token = (String) ((JavascriptExecutor) driver)
                    .executeScript("return window.localStorage.getItem('token');");
            if (token == null || token.isEmpty()) {
                throw new RuntimeException("❌ No Bearer token found in localStorage after UI login.");
            }
            TestBase.initDataFactory(token);

            DebugUtils.logSessionToken(driver, "After Login");
            DebugUtils.logBrowserConsole(driver, "After Login");

            String moduleLabel = System.getProperty("module", "Purchase");
            String menuLabel = System.getProperty("menu", "Purchase");
            String subMenuLabel = System.getProperty("submenu", "Purchase Order");

            nav.goTo(moduleLabel, menuLabel, subMenuLabel);

            System.out.println("[DEBUG] URL after submenu click: " + driver.getCurrentUrl());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h3[contains(@class, 'card-title') and contains(@class, 'fw-bold') and contains(text(),'Purchase Order Summary')]")));
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Direct PO')]")));
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(.,'Direct PO') and @data-bs-target='#myModaladd']")));

            System.out.println("[DEBUG] ✅ Direct PO button is clickable.");

            takeScreenshot("step_direct_po_btn_clickable.png");

            directPOPage = new DirectPO(driver);

            // Open the modal and confirm it stays open by waiting for a field inside it
            directPOPage.openDirectPOModal(rootTest);
            System.out.println("[DEBUG] Direct PO modal opened, waiting for branch dropdown...");
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//ng-select[@formcontrolname='branch_name']")));
            System.out.println("[DEBUG] Modal ready for input!");

            // Dump browser logs in case of JS errors on modal open
            DebugUtils.logBrowserConsole(driver, "After Direct PO Modal Open");

            ValidationLogger.reset();
            System.out.println("[DEBUG] Token is: " + token);
            if (token == null || token.isEmpty()) {
                System.out.println("About to throw exception due to missing token...");
                throw new RuntimeException("❌ No Bearer token found in localStorage after UI login.");
            }

            // =========== MASTERS FETCH AND VALIDATION SECTION ============
            ApiMasterDataProvider apiProvider = new ApiMasterDataProvider(BASE_URL, token);
            allMasters = new MasterData();
            allMasters.branches   = apiProvider.getAllBranchObjects();
            allMasters.employees  = apiProvider.getEmployees();
            allMasters.vendors    = apiProvider.getVendors();
            allMasters.products   = apiProvider.getProducts();
            allMasters.taxes      = apiProvider.getAllTaxObjects();
            allMasters.terms      = apiProvider.getAllTermsObjects();
            allMasters.currencies = apiProvider.getAllCurrencyObjects();

            // 2. Validate all are non-empty and log the result (fail fast if any is missing)
            validateAndLogMasters(allMasters);

            System.out.println("[DEBUG] === setupTest END ===");
        } catch (Throwable t) {
            System.out.println("[ERROR] Exception in setupTest: " + t);
            t.printStackTrace();
            takeScreenshot("setupTest_error.png");
            throw new RuntimeException("setupTest failed: " + t.getMessage(), t);
        }
    }

    // --- Validation Helper ---
    private void validateAndLogMasters(MasterData masterData) {
        StringBuilder sb = new StringBuilder("\n========== Master Data Fetch Summary ==========\n");
        if (masterData.branches == null || masterData.branches.isEmpty())
            throw new RuntimeException("❌ Branch master data not fetched!");
        sb.append(String.format("Branches: %d\n", masterData.branches.size()));

        if (masterData.employees == null || masterData.employees.isEmpty())
            throw new RuntimeException("❌ Employee master data not fetched!");
        sb.append(String.format("Employees: %d\n", masterData.employees.size()));

        if (masterData.vendors == null || masterData.vendors.isEmpty())
            throw new RuntimeException("❌ Vendor master data not fetched!");
        sb.append(String.format("Vendors: %d\n", masterData.vendors.size()));

        if (masterData.products == null || masterData.products.isEmpty())
            throw new RuntimeException("❌ Product master data not fetched!");
        sb.append(String.format("Products: %d\n", masterData.products.size()));

        if (masterData.taxes == null || masterData.taxes.isEmpty())
            throw new RuntimeException("❌ Tax master data not fetched!");
        sb.append(String.format("Taxes: %d\n", masterData.taxes.size()));

        if (masterData.terms == null || masterData.terms.isEmpty())
            throw new RuntimeException("❌ Terms & Conditions master data not fetched!");
        sb.append(String.format("Terms: %d\n", masterData.terms.size()));

        if (masterData.currencies == null || masterData.currencies.isEmpty())
            throw new RuntimeException("❌ Currency master data not fetched!");
        sb.append(String.format("Currencies: %d\n", masterData.currencies.size()));

        sb.append("===============================================");
        System.out.println(sb.toString());
    }

    @Test(description = "Create and submit a direct purchase order (fully dynamic)")
    public void testCreateAndSubmitDirectPO() {
        try {
            System.out.println("[DEBUG] testCreateAndSubmitDirectPO() ENTERED");
            ExtentTest dataNode = ExtentTestManager.createNode("🛠 Generate PO Test Data");
            PurchaseOrderData poData = factory.create(false);
            dataNode.pass("✅ Successfully generated PurchaseOrderData");

            ExtentTest fillNode = ExtentTestManager.createNode("📝 Fill Direct PO Form");
            directPOPage.fillForm(poData, fillNode);

            ExtentTest submitNode = ExtentTestManager.createNode("🚀 Submit & Capture Ref");
            String poRef = directPOPage.submitAndCaptureRef(submitNode);

            ValidationLogger.assertTrue(
                    "PO reference generated",
                    poRef != null && !poRef.isEmpty(),
                    submitNode
            );
            System.out.println("[DEBUG] testCreateAndSubmitDirectPO() EXIT");
        } catch (Throwable t) {
            t.printStackTrace();
            throw new RuntimeException("Test failed due to uncaught exception", t);
        }
    }


    @AfterMethod(alwaysRun = true)
    public void tearDownTest(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            rootTest.fail("❌ Test failed; see details below");
        }
        TestContextLogger.logTestEnd("DirectPOTest");
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        ExtentTestManager.flushReports();
    }

    // Utility to take a screenshot
    private void takeScreenshot(String filename) {
        try {
            File src = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            Files.copy(src.toPath(), Paths.get(filename));
            System.out.println("[DEBUG] Screenshot taken: " + filename);
        } catch (Exception e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
        }
    }
}
