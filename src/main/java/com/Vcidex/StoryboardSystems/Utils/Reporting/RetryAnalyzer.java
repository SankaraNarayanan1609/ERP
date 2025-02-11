package com.Vcidex.StoryboardSystems.Utils.Reporting;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private int retryCount = 0;
    private static final int maxRetryCount = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            logger.warn("🔄 Retrying failed test: {} (Retry {}/{})", result.getName(), retryCount + 1, maxRetryCount);
            retryCount++;
            return true;
        }
        return false;
    }
}