// DirectInvoiceLogger.java
package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DirectInvoiceLogger {
    private static final Logger logger = LoggerFactory.getLogger("DirectInvoiceBusiness");
    private static final ThreadLocal<String> INV_ID = new ThreadLocal<>();

    public static void start(String vendor) {
        String temp = "INV-temp-" + Thread.currentThread().getId();
        INV_ID.set(temp);
        logger.info("[Inv:{}] Starting Direct Invoice creation for Vendor: {}", temp, vendor);
    }

    public static void addedGeneralDetails(String type, String location) {
        logger.debug("[Inv:{}] General details – Type: {}, Location: {}", INV_ID.get(), type, location);
    }

    public static void addedProduct(String name, int qty) {
        logger.debug("[Inv:{}] Added product – {} x{}", INV_ID.get(), name, qty);
    }

    public static void addedMultipleProducts(int count) {
        logger.debug("[Inv:{}] Added {} products total", INV_ID.get(), count);
    }

    public static void submitting() {
        logger.info("[Inv:{}] Submitting the Direct Invoice form...", INV_ID.get());
    }

    public static void creationSuccess(String invNumber) {
        INV_ID.set(invNumber);
        logger.info("[Inv:{}] {}", invNumber, PurchaseLogs.Invoice.created(invNumber));
    }

    public static void validatingSummary(String expected) {
        logger.info("[Inv:{}] Validating Invoice# in summary: expected={}", INV_ID.get(), expected);
    }

    public static void validationSuccess(String actual) {
        logger.info("[Inv:{}] Invoice summary OK – found={}", INV_ID.get(), actual);
    }

    public static void validationFailure(String expected, String actual) {
        logger.warn("[Inv:{}] Invoice summary MISMATCH – expected={} found={}",
                INV_ID.get(), expected, actual);
    }

    public static void error(String step, Throwable t) {
        logger.error("[Inv:{}] {}", INV_ID.get(), PurchaseLogs.Invoice.error(step, t), t);
    }

    /** Example: business‐level metric hook */
    public static void metricItems(int count) {
        logger.info("[Inv:{}][METRIC] InvoiceItems count={}", INV_ID.get(), count);
    }
}


