package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectInvoiceLogger {
    private static final Logger logger = LoggerFactory.getLogger(DirectInvoiceLogger.class);

    public static void startDirInvCreation(String vendorName) {
        logger.info("[Inv] Starting Direct Invoice creation for Vendor: {}", vendorName);
    }

    public static void addedGeneralDetails(String invType, String deliveryLocation) {
        logger.debug("[Inv] Entered general details - Inv Type: {}, Delivery Location: {}", invType, deliveryLocation);
    }

    public static void addedProduct(String productName, int quantity) {
        logger.debug("[Inv] Added product - Name: {}, Quantity: {}", productName, quantity);
    }

    public static void addedMultipleProducts(int count) {
        logger.debug("[Inv] Added {} products in total.", count);
    }

    public static void submittingDirInv() {
        logger.info("[Inv] Submitting the Direct Invoice form...");
    }

    public static void dirInvCreationSuccess(String invNumber) {
        logger.info(PurchaseLogs.Invoice.created(invNumber));
    }

    public static void validatingDirInvSummary(String expectedInvNumber) {
        logger.info("[Inv] Validating Invoice number in summary: Expected = {}", expectedInvNumber);
    }

    public static void validationSuccess(String actualInvNumber) {
        logger.info("[Inv] Invoice Summary validation passed. Invoice Number matched: {}", actualInvNumber);
    }

    public static void validationFailure(String expected, String actual) {
        logger.warn("[Inv] Invoice Summary validation failed. Expected: {}, Found: {}", expected, actual);
    }

    public static void errorDuringDirInvCreation(String step, Throwable t) {
        logger.error(PurchaseLogs.Invoice.error(step, t), t);
    }

    public static void info(String message) {
        logger.info("[Inv] {}", message);
    }

    public static void warn(String message) {
        logger.warn("[Inv] {}", message);
    }

    public static void error(String message, Throwable t) {
        logger.error("[Inv] {}", message, t);
    }
}