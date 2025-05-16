package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectPurchaseOrderLogger {
    private static final Logger logger = LoggerFactory.getLogger("PurchaseOrderBusiness");

    public static void startPOCreation(String vendor) {
        logger.info("[PO] Starting PO creation for Vendor: {}", vendor);
    }

    public static void addedGeneralDetails(String poType, String location) {
        logger.debug("[PO] General details – Type: {}, Location: {}", poType, location);
    }

    public static void addedProduct(String product, int qty) {
        logger.debug("[PO] Added product – {} x{}", product, qty);
    }

    public static void addedMultipleProducts(int count) {
        logger.debug("[PO] Added {} products total", count);
    }

    public static void submittingPO() {
        logger.info("[PO] Submitting PO form...");
    }

    public static void poCreationSuccess(String poNum, String vendor) {
        logger.info(PurchaseLogs.PO.created(poNum, vendor));
    }

    public static void poApproved(String poNum, String approver) {
        logger.info(PurchaseLogs.PO.approved(poNum, approver));
    }

    public static void poCancelled(String poNum, String reason) {
        logger.info(PurchaseLogs.PO.cancelled(poNum, reason));
    }

    public static void validatingPOSummary(String expected) {
        logger.info("[PO] Validating PO# in summary: expected={}", expected);
    }

    public static void validationSuccess(String actual) {
        logger.info("[PO] PO summary OK – found={}", actual);
    }

    public static void validationFailure(String expected, String actual) {
        logger.warn("[PO] PO summary MISMATCH – expected={} found={}", expected, actual);
    }

    public static void poError(String poNum, String ctx, Throwable t) {
        logger.error(PurchaseLogs.PO.error(poNum, ctx), t);
    }

    public static void errorDuring(String step, Throwable t) {
        logger.error("[PO] Error during {}: {}", step, t.getMessage(), t);
    }
}