// PurchaseIndentLogger.java
package com.Vcidex.StoryboardSystems.Purchase.Business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurchaseIndentLogger {
    private static final Logger logger = LoggerFactory.getLogger("RaisePurchaseIndentBusiness");
    private static final ThreadLocal<String> PI_ID = new ThreadLocal<>();

    public static void start(String branch, String requestedBy, String dept, String remarks) {
        String temp = "PI-temp-" + Thread.currentThread().getId();
        PI_ID.set(temp);
        logger.info("[PI:{}] Starting Raise PI – Branch: {} | RequestedBy: {} | Dept: {} | Remarks: {}",
                temp, branch, requestedBy, dept, remarks);
    }

    public static void addedGeneralDetails(String type, String location) {
        logger.debug("[PI:{}] General details – Type: {}, Location: {}", PI_ID.get(), type, location);
    }

    public static void addedProduct(String name, int qty) {
        logger.debug("[PI:{}] Added product – {} x{}", PI_ID.get(), name, qty);
    }

    public static void addedMultipleProducts(int count) {
        logger.debug("[PI:{}] Added {} products total", PI_ID.get(), count);
    }

    public static void submitting() {
        logger.info("[PI:{}] Submitting the Purchase Indent form...", PI_ID.get());
    }

    public static void creationSuccess(String piNumber) {
        PI_ID.set(piNumber);
        logger.info("[PI:{}] Purchase Indent created – PI Number: {}", piNumber, piNumber);
    }

    public static void validatingSummary(String expected) {
        logger.info("[PI:{}] Validating PI# in summary: expected={}", PI_ID.get(), expected);
    }

    public static void validationSuccess(String actual) {
        logger.info("[PI:{}] PI summary OK – found={}", PI_ID.get(), actual);
    }

    public static void validationFailure(String expected, String actual) {
        logger.warn("[PI:{}] PI summary MISMATCH – expected={} found={}",
                PI_ID.get(), expected, actual);
    }

    public static void error(String step, Throwable t) {
        logger.error("[PI:{}] Error during {}: {}", PI_ID.get(), step, t.getMessage(), t);
    }

    public static void metricLines(int count) {
        logger.info("[PI:{}][METRIC] IndentLines count={}", PI_ID.get(), count);
    }
}