package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Inventory.Navigation.MaterialInwardNavigator;
import com.Vcidex.StoryboardSystems.Listeners.LogoutDetector;
import com.Vcidex.StoryboardSystems.LoginManager;
import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.DirectPONavigator;
import com.Vcidex.StoryboardSystems.Purchase.POJO.MasterData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.TestBase;
import com.Vcidex.StoryboardSystems.Utils.DebugUtils;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import com.Vcidex.StoryboardSystems.Utils.MasterDataLoader;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.ITestResult;
import org.testng.annotations.*;

@Listeners(LogoutDetector.class)
public class DirectPOTest extends TestBase {
    private static final String BASE_URL    = "https://erplite.storyboarderp.com";

    private NavigationManager       nav;
    private DirectPONavigator       poNav;
    private MaterialInwardNavigator inwardNav;
    private ExtentTest              rootTest;
    private MasterData              allMasters;

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
        // 1) create root test
        rootTest = ExtentTestManager.createTest(
                "DirectPO + Material Inward Flow",
                "E2E Purchase + Inward"
        );

        // 2) init navigators
        poNav     = new DirectPONavigator(driver, nav, rootTest);
        inwardNav = new MaterialInwardNavigator(driver, nav, rootTest);

        // --- Login via UI ---
        ExtentTest loginNode = ExtentTestManager.createNode("🔑 Login");
        new LoginManager(driver, loginNode)
                .loginViaUi(appName, companyCode, userId);
        DebugUtils.waitForAngular(driver);

        // 3) fetch & validate token
        String token = (String)((JavascriptExecutor)driver)
                .executeScript("return window.localStorage.getItem('token');");
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("❌ No token in localStorage after login");
        }
        TestBase.initDataFactory(token);

        // 4) load & validate master data
        allMasters = new MasterDataLoader(BASE_URL, token)
                .loadAndValidate();
    }

    @Test(description = "Create Direct PO → then Material Inward")
    public void testCreateDirectPOThenMaterialInward() {
        // ── Direct PO flow ──
        DirectPO poPage = poNav.openDirectPO();

        ExtentTest poDataNode = ExtentTestManager.createNode("🛠 PO Data");
        PurchaseOrderData poData = factory.create(false);
        poDataNode.pass("✅ PO Ref (to create): " + poData.getPoRefNo());

        ExtentTest poFillNode = ExtentTestManager.createNode("📝 Fill Direct PO");
        poPage.fillForm(poData, poFillNode);

        ExtentTest poSubmitNode = ExtentTestManager.createNode("🚀 Submit & Capture Direct PO");
        String poRef = poPage.submitAndCaptureRef(poSubmitNode);


        // ── Material Inward flow via navigator ──
        var miPage = inwardNav.openSelectPurchaseOrderScreen();
        inwardNav.selectPurchaseOrder(miPage, poRef);
        inwardNav.openAddInwardModal(miPage);

        ExtentTest miDataNode = ExtentTestManager.createNode("🛠 Inward Data");
        var miData = com.Vcidex.StoryboardSystems.Inventory.MaterialInwardDataFactory.create();
        miDataNode.pass("✅ DC No: " + miData.getDcNo());

        ExtentTest miFillNode = ExtentTestManager.createNode("📝 Fill Material Inward");
        miPage.fillForm(miData, miFillNode);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTest(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            rootTest.fail("❌ Test failed: " + result.getThrowable());
        }
        TestContextLogger.logTestEnd("DirectPOTest");
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        ExtentTestManager.flushReports();
    }
}