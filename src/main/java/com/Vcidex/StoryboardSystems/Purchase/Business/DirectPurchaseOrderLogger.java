package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectPurchaseOrderLogger {

    private static final Logger logger = LoggerFactory.getLogger("PurchaseOrderBusiness");

    public static void startPOCreation(String vendorName) {
        logger.info("[PO] Starting Direct Purchase Order creation for Vendor: {}", vendorName);
    }

    public static void addedGeneralDetails(String poType, String deliveryLocation) {
        logger.debug("[PO] Entered general details - PO Type: {}, Delivery Location: {}", poType, deliveryLocation);
    }

    public static void addedProduct(String productName, int quantity) {
        logger.debug("[PO] Added product - Name: {}, Quantity: {}", productName, quantity);
    }

    public static void addedMultipleProducts(int count) {
        logger.debug("[PO] Added {} products in total.", count);
    }

    public static void submittingPO() {
        logger.info("[PO] Submitting the Purchase Order form...");
    }

    public static void poCreationSuccess(String poNumber, String vendor) {
        logger.info(PurchaseLogs.PO.created(poNumber, vendor));
    }

    public static void poApproved(String poNumber, String approver) {
        logger.info(PurchaseLogs.PO.approved(poNumber, approver));
    }

    public static void poCancelled(String poNumber, String reason) {
        logger.info(PurchaseLogs.PO.cancelled(poNumber, reason));
    }

    public static void poError(String poNumber, String context, Throwable t) {
        logger.error(PurchaseLogs.PO.error(poNumber, context), t);
    }

    public static void validatingPOSummary(String expectedPoNumber) {
        logger.info("[PO] Validating PO number in summary: Expected = {}", expectedPoNumber);
    }

    public static void validationSuccess(String actualPoNumber) {
        logger.info("[PO] PO Summary validation passed. PO Number matched: {}", actualPoNumber);
    }

    public static void validationFailure(String expected, String actual) {
        logger.warn("[PO] PO Summary validation failed. Expected: {}, Found: {}", expected, actual);
    }

    public static void errorDuringPOCreation(String step, Throwable t) {
        logger.error("[PO] Error during step: {}. Exception: {}", step, t.getMessage(), t);
    }

    public static void info(String message) {
        logger.info("[PO] {}", message);
    }

    public static void warn(String message) {
        logger.warn("[PO] {}", message);
    }

    public static void error(String message, Throwable t) {
        logger.error("[PO] {}", message, t);
    }
}