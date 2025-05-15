package com.Vcidex.StoryboardSystems.Purchase.Business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurchaseIndentLogger {

    private static final Logger logger = LoggerFactory.getLogger("RaisePurchaseIndentBusiness");

    public static void startPICreation(String branchName, String requestedBy, String department, String remarks) {
        logger.info("[RaisePI] Starting Raise PI creation - Branch: {}, Requested By: {}, Department: {}, Remarks: {}",
                branchName, requestedBy, department, remarks);
    }

    public static void addedGeneralDetails(String piType, String deliveryLocation) {
        logger.debug("[PI] Entered general details - PI Type: {}, Delivery Location: {}", piType, deliveryLocation);
    }

    public static void addedProduct(String productName, int quantity) {
        logger.debug("[PI] Added product - Name: {}, Quantity: {}", productName, quantity);
    }

    public static void addedMultipleProducts(int count) {
        logger.debug("[PI] Added {} products in total.", count);
    }

    public static void submittingPI() {
        logger.info("[PI] Submitting the Purchase Indent form...");
    }

    public static void piCreationSuccess(String piNumber) {
        logger.info("[PI] Purchase Indent created successfully. PI Number: {}", piNumber);
    }

    public static void validatingPISummary(String expectedPINumber) {
        logger.info("[PI] Validating PI number in summary: Expected = {}", expectedPINumber);
    }

    public static void validationSuccess(String actualPINumber) {
        logger.info("[PI] PI Summary validation passed. PI Number matched: {}", actualPINumber);
    }

    public static void validationFailure(String expected, String actual) {
        logger.warn("[PI] PI Summary validation failed. Expected: {}, Found: {}", expected, actual);
    }

    public static void errorDuringPICreation(String step, Throwable t) {
        logger.error("[PI] Error during step: {}. Exception: {}", step, t.getMessage(), t);
    }

    public static void info(String message) {
        logger.info("[PI] {}", message);
    }

    public static void warn(String message) {
        logger.warn("[PI] {}", message);
    }

    public static void error(String message, Throwable t) {
        logger.error("[PI] {}", message, t);
    }
}