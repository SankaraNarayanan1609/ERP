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

@Listeners(LogoutDetector.class)
public class DirectPOTest extends TestBase {
    private NavigationManager nav;
    private DirectPO directPOPage;
    private ExtentTest rootTest;

    // Make timeout easy to change or parametrize
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
        rootTest = ExtentTestManager.createTest("DirectPO Creation & Submit", "Purchase");
        ExtentTest loginNode = ExtentTestManager.createNode("🔑 Login");

        new LoginManager(driver, loginNode).loginViaUi(appName, companyCode, userId);

        // Always prefer explicit wait over Thread.sleep
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

        System.out.println("URL after submenu click: " + driver.getCurrentUrl());

        // Use a single explicit wait
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT));

        // Wait for header indicating correct page
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//h4[contains(text(),'Purchase Order')]")));

        // Wait for Direct PO button to appear/clickable
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Direct PO')]")));

        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(.,'Direct PO') and @data-bs-target='#myModaladd']")));

        System.out.println("✅ Direct PO button is clickable.");

        takeScreenshot("step_direct_po_btn_clickable.png");

        directPOPage = new DirectPO(driver);

        // Open the modal explicitly before fillForm!
        directPOPage.openDirectPOModal(rootTest);

        ValidationLogger.reset();
    }

    @Test(description = "Create and submit a direct purchase order (fully dynamic)")
    public void testCreateAndSubmitDirectPO() {
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
        } catch (Exception e) {
            System.out.println("Failed to capture screenshot: " + e.getMessage());
        }
    }
}
