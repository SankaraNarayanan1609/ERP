package com.Vcidex.StoryboardSystems.Listeners;

import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

public class LogoutDetector implements IInvokedMethodListener {
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        if (driver == null) return;
        String url = driver.getCurrentUrl();
        boolean onLoginPage = url.contains("/Login")
                || !driver.findElements(By.id("kt_sign_in_submit")).isEmpty();
        if (onLoginPage) {
            UIActionLogger.failure(driver,
                    "Detected redirect to Login after "
                            + method.getTestMethod().getMethodName());
        }
    }
}