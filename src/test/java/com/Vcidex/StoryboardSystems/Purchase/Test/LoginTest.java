package com.Vcidex.StoryboardSystems.Purchase.Test;

import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentTestManager;
import com.aventstack.extentreports.ExtentTest;
import org.testng.annotations.Test;

public class LoginTest extends TestBase {

    @Test
    public void verifyLogin() {
        LoginManager login = new LoginManager(driver);
        login.login("test", 0);

        // ✅ Get the test instance from ExtentTestManager
        ExtentTest extentTest = ExtentTestManager.getTest();
        extentTest.info("Login test executed.");  // ✅ Now works
    }
}
