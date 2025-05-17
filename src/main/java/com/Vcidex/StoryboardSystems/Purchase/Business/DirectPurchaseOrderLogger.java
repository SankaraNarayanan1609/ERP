package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectPurchaseOrderLogger {
    private static final Logger logger = LoggerFactory.getLogger("PurchaseOrderBusiness");
    private static final ThreadLocal<String> PO_ID = new ThreadLocal<>();

    public static void startPOCreation(String vendor) {
        String tempId = "PO-temp-" + Thread.currentThread().getId();
        PO_ID.set(tempId);
        logger.info("[PO:{}] Starting PO creation for Vendor: {}", tempId, vendor);
    }

    public static void addedGeneralDetails(String poType, String location) {
        logger.debug("[PO:{}] General details – Type: {}, Location: {}", PO_ID.get(), poType, location);
    }

    public static void addedProduct(String product, int qty) {
        logger.debug("[PO:{}] Added product – {} x{}", PO_ID.get(), product, qty);
    }

    public static void addedMultipleProducts(int count) {
        logger.debug("[PO:{}] Added {} products total", PO_ID.get(), count);
    }

    public static void submittingPO() {
        logger.info("[PO:{}] Submitting PO form...", PO_ID.get());
    }

    public static void poCreationSuccess(String poNum, String vendor) {
        PO_ID.set(poNum);
        logger.info("[PO:{}] {}", poNum, PurchaseLogs.PO.created(poNum, vendor));
    }

    public static void poApproved(String poNum, String approver) {
        logger.info("[PO:{}] {}", PO_ID.get(), PurchaseLogs.PO.approved(poNum, approver));
    }

    public static void poCancelled(String poNum, String reason) {
        logger.info("[PO:{}] {}", PO_ID.get(), PurchaseLogs.PO.cancelled(poNum, reason));
    }

    public static void validatingPOSummary(String expected) {
        logger.info("[PO:{}] Validating PO# in summary: expected={}", PO_ID.get(), expected);
    }

    public static void validationSuccess(String actual) {
        logger.info("[PO:{}] PO summary OK – found={}", PO_ID.get(), actual);
    }

    public static void validationFailure(String expected, String actual) {
        logger.warn("[PO:{}] PO summary MISMATCH – expected={} found={}", PO_ID.get(), expected, actual);
    }

    public static void poError(String ctx, Throwable t) {
        logger.error("[PO:{}] {}", PO_ID.get(), PurchaseLogs.PO.error(PO_ID.get(), ctx), t);
    }

    public static void errorDuring(String step, Throwable t) {
        logger.error("[PO:{}] Error during {}: {}", PO_ID.get(), step, t.getMessage(), t);
    }

    /** Example of business metric hook */
    public static void metricItems(int count) {
        logger.info("[METRIC] POItems count={}", count);
    }
}