// src/test/java/com/Vcidex/StoryboardSystems/PurchaseTest/DirectPOTest.java
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
import org.testng.ITestResult;
import org.testng.annotations.*;

@Listeners(LogoutDetector.class)

public class DirectPOTest extends TestBase {
    private NavigationManager nav;
    private DirectPO          directPOPage;
    private ExtentTest        rootTest;

    @BeforeClass
    public void beforeClass() {
        nav = new NavigationManager(driver);
    }

    @BeforeMethod
    @Parameters({ "appName", "companyCode", "userId" })
    public void setupTest(
            @Optional("StoryboardSystems") String appName,
            @Optional("vcidex")            String companyCode,
            @Optional("Superadmin")        String userId
    ) {
        rootTest = ExtentTestManager.createTest("DirectPO Creation & Submit", "Purchase");
        ExtentTest loginNode = ExtentTestManager.createNode("🔑 Login");
        new LoginManager(driver, loginNode).login(appName, companyCode, userId);

        DebugUtils.waitForAngular(driver);
        DebugUtils.logSessionToken(driver, "After Login");
        DebugUtils.logBrowserConsole(driver, "After Login");

        driver.navigate().refresh();

        nav.goTo("Purchase", "Purchase", "Purchase Order");

        directPOPage = new DirectPO(driver);
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
}