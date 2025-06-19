// RetryAnalyzer.java
package com.Vcidex.StoryboardSystems.Utils.Logger;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Handles retry and recovery logging separately.
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private int retryCount = 0;
    private static final int maxRetry = 1;  // Retry only once

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetry) {
            retryCount++;
            return true;
        }
        return false;
    }
}