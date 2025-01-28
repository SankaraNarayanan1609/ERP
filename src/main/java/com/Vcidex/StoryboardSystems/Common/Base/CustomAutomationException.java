package com.Vcidex.StoryboardSystems.Common.Base;

import org.apache.log4j.Logger;

public class CustomAutomationException extends Exception {
    private static final Logger logger = Logger.getLogger(CustomAutomationException.class);

    // Additional field to store error code (if needed)
    private String errorCode;
    private long timestamp;

    // Constructor with just a message
    public CustomAutomationException(String message) {
        super(message);
        this.timestamp = System.currentTimeMillis(); // Capture the timestamp of when the exception is thrown
        logException(message);
    }

    // Constructor with message and cause
    public CustomAutomationException(String message, Throwable cause) {
        super(message, cause);
        this.timestamp = System.currentTimeMillis(); // Capture the timestamp of when the exception is thrown
        logException(message);
    }

    // Constructor with message, cause, and an error code
    public CustomAutomationException(String message, Throwable cause, String errorCode) {
        super(message, cause);
        this.timestamp = System.currentTimeMillis(); // Capture the timestamp of when the exception is thrown
        this.errorCode = errorCode;
        logException(message);
    }

    // Method to log exception details
    private void logException(String message) {
        logger.error("Exception occurred at " + timestamp + " with message: " + message);
        if (errorCode != null) {
            logger.error("Error Code: " + errorCode);
        }
    }

    // Getters for errorCode and timestamp if needed for further processing
    public String getErrorCode() {
        return errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
