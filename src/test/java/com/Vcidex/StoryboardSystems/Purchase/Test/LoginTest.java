package com.Vcidex.StoryboardSystems.Purchase.Test;

import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import org.testng.annotations.Test;

public class LoginTest extends TestBase {

    @Test
    public void verifyLogin() {
        LoginManager login = new LoginManager(driver);
        login.login("test", 0);

        test.info("Login test executed.");
    }
}
