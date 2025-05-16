package com.Vcidex.StoryboardSystems.Purchase.DirectPO_Flow;

import com.Vcidex.StoryboardSystems.BaseTest;
import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationData;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationHelper;
import com.Vcidex.StoryboardSystems.Utils.Logger.ExtentTestManager;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.util.Map;

public class DirectPOTest extends BaseTest {

    private LoginManager loginManager;
    private Direct_PO directPO;
    private NavigationHelper navigationHelper;

    // ✅ Corrected based on your config.json
    private final String environment = "test"; // should match your JSON root
    private final String userId = "0";         // matches the userId in config.json

    @BeforeClass
    public void setUpTest() {
        super.setUpDriver();

        // 🔥 Initialize ExtentTest first
        ExtentTestManager.createTest("Direct PO Test - Setup");

        // Set driver in ThreadSafeDriverManager
        ThreadSafeDriverManager.setDriver(driver);

        loginManager = new LoginManager(driver);
        directPO = new Direct_PO(driver);
        navigationHelper = new NavigationHelper(driver);

        Map<String, String> userCredentials = ConfigManager.getUserById(environment, userId);
        String loginUserId = userCredentials.get("userId");

        Reporter.log("🔐 Logging in as userId: " + loginUserId, true);
        loginManager.login(loginUserId);  // Now this won't throw Extent error

        Reporter.log("📦 Navigating to Purchase Order module", true);
        navigationHelper.navigateToModuleAndMenu(NavigationData.PO);
    }

    @Test(priority = 1, dataProvider = "SingleScenarioProvider", dataProviderClass = DataProviderManager.class)
    public void testCreateDirectPO(String scenarioID, Map<String, String> data) {
        Reporter.log("🚧 Starting Direct PO Creation", true);

        if (data.get("PORefNo") != null && !data.get("PORefNo").isEmpty()) directPO.enterPORefNo(data.get("PORefNo"));
        if (data.get("PODate") != null && !data.get("PODate").isEmpty()) directPO.selectPODate(data.get("PODate"));
        if (data.get("ExpectedDate") != null && !data.get("ExpectedDate").isEmpty()) directPO.selectExpectedDate(data.get("ExpectedDate"));
        if (data.get("BillTo") != null && !data.get("BillTo").isEmpty()) directPO.enterBillTo(data.get("BillTo"));
        if (data.get("RequestedBy") != null && !data.get("RequestedBy").isEmpty()) directPO.selectRequestedBy(data.get("RequestedBy"));
        if (data.get("RequestorContact") != null && !data.get("RequestorContact").isEmpty()) directPO.enterRequestorContact(data.get("RequestorContact"));
        if (data.get("DeliveryTerms") != null && !data.get("DeliveryTerms").isEmpty()) directPO.enterDeliveryTerms(data.get("DeliveryTerms"));
        if (data.get("PaymentTerms") != null && !data.get("PaymentTerms").isEmpty()) directPO.enterPaymentTerms(data.get("PaymentTerms"));
        if (data.get("DespatchMode") != null && !data.get("DespatchMode").isEmpty()) directPO.enterDespatchMode(data.get("DespatchMode"));
        if (data.get("Currency") != null && !data.get("Currency").isEmpty()) directPO.selectCurrency(data.get("Currency"));
        if (data.get("CoverNote") != null && !data.get("CoverNote").isEmpty()) directPO.enterCoverNote(data.get("CoverNote"));
        if (data.get("IsRenewal") != null && !data.get("IsRenewal").isEmpty()) directPO.selectRenewal(Boolean.parseBoolean(data.get("IsRenewal")));
        if (data.get("RenewalDate") != null && !data.get("RenewalDate").isEmpty()) directPO.selectRenewalDate(data.get("RenewalDate"));
        if (data.get("Frequency") != null && !data.get("Frequency").isEmpty()) directPO.selectFrequency(data.get("Frequency"));

        directPO.clickAddProduct(); // assuming this is mandatory

        if (data.get("Terms") != null && !data.get("Terms").isEmpty()) {
            directPO.selectTermsDropdown(data.get("Terms"));
        } else {
            Reporter.log("⚠️ Terms missing in Excel. Skipping terms selection.", true);
        }

        directPO.clickSaveAsDraft();
        Reporter.log("✅ Direct PO saved as draft", true);
    }

    @Test(priority = 2)
    public void submitDirectPO() {
        Reporter.log("🚀 Submitting Direct PO", true);
        directPO.clickSubmit();
        Assert.assertTrue(directPO.isPOSubmitted(), "❌ PO submission failed!");
        Reporter.log("✅ PO submitted successfully", true);
    }

    @Test(priority = 3)
    public void cancelDirectPO() {
        Reporter.log("❌ Cancelling Direct PO", true);
        directPO.clickCancel();
        Assert.assertTrue(directPO.isPOCancelled(), "❌ PO cancellation failed!");
        Reporter.log("✅ PO cancelled successfully", true);
    }

    @AfterClass
    public void tearDown() {
        Reporter.log("🧹 Cleaning up and closing browser", true);
        if (driver != null) {
            driver.quit();
        }
    }
}