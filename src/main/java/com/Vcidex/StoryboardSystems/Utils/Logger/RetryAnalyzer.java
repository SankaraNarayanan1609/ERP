// RetryAnalyzer.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Handles retry and recovery logging separately.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;
    private static final int maxRetry = 1;

    public static int getMaxRetry() { return maxRetry; }

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetry) {
            result.setAttribute("retry", true);
            result.setAttribute("retryCount", retryCount);
            retryCount++;
            return true;
        }
        return false;
    }
}
