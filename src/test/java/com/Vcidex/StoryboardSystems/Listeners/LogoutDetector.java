// File: src/main/java/com/Vcidex/StoryboardSystems/Listeners/LogoutDetector.java
package com.Vcidex.StoryboardSystems.Listeners;

import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class LogoutDetector implements IInvokedMethodListener {
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        if (driver == null) {
            return;
        }

        String url = driver.getCurrentUrl();
        boolean onLoginPage =
                url.contains("/Login")
                        || !driver.findElements(By.id("kt_sign_in_submit")).isEmpty();

        if (onLoginPage) {
            // Previously: UIActionLogger.failure(...)
            // Now delegate directly to DiagnosticsLogger for consistent failure handling
            DiagnosticsLogger.onFailure(
                    driver,
                    "Detected unexpected redirect to login after "
                            + method.getTestMethod().getMethodName()
            );
        }
    }
}
