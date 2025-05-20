package com.Vcidex.StoryboardSystems.PurchaseTest;

import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.TestContextLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.TestBase;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

public class DirectPOTest extends TestBase {
    private DirectPO directPOPage;

    @BeforeMethod
    @Parameters({"appName","companyCode","userId"})
    public void setupTest(
            @Optional("StoryboardSystems") String appName,
            @Optional("vcidex")         String companyCode,
            @Optional("Superadmin")     String userId
    ) {
        ExtentTestManager.createTest("DirectPO Creation & Submit", "Purchase");
        TestContextLogger.logTestStart("DirectPOTest", driver); // Cannot resolve symbol 'driver'

        new LoginManager(driver).login(appName, companyCode, userId);
        directPOPage = new DirectPO(driver);
        ValidationLogger.reset();
    }

    @Test(description = "Create and submit a direct purchase order (fully dynamic)")
    public void testCreateAndSubmitDirectPO() {
        // **ZERO hard-coding**: factory picks everything at random
        PurchaseOrderData poData = factory.create(/* renewalRequired= */ false);

        directPOPage.fillForm(poData);
        String poRef = directPOPage.submitAndCaptureRef();

        ValidationLogger.assertTrue(
                "PO reference generated",
                poRef != null && !poRef.isEmpty(),
                driver
        );
    }

    @AfterMethod
    public void tearDownTest() {
        TestContextLogger.logTestEnd("DirectPOTest");
    }

    @AfterClass(alwaysRun = true)
    public void tearDownSuite() {
        ExtentTestManager.flushReports();
        driver.quit();
        ThreadSafeDriverManager.removeDriver();
    }
}
