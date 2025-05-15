package com.Vcidex.StoryboardSystems.Purchase.Business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;

public class ReceiveServiceInvoiceLogger {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveServiceInvoiceLogger.class);

    public static void startReceiveInvoice(String invoiceRefNo) {
        logger.info(PurchaseLogs.Invoice.created(invoiceRefNo));
    }

    public static void validatingField(String fieldName, String expected, String actual) {
        if (expected.equals(actual)) {
            logger.debug("[RecvInv] Field matched - {}: {}", fieldName, actual);
        } else {
            logger.warn(PurchaseLogs.Invoice.error(fieldName,
                    new Throwable(String.format("Expected: %s, Found: %s", expected, actual))));
        }
    }

    public static void validatingProductLineItem(String productCode, int expectedQty, int actualQty, double expectedPrice, double actualPrice) {
        logger.debug("[RecvInv] Validating Product - Code: {}", productCode);
        if (expectedQty == actualQty && expectedPrice == actualPrice) {
            logger.debug("[RecvInv] Product matched - Qty: {}, Price: {}", actualQty, actualPrice);
        } else {
            logger.warn("[RecvInv] Product mismatch - Qty[Expected: {}, Found: {}], Price[Expected: {}, Found: {}]",
                    expectedQty, actualQty, expectedPrice, actualPrice);
        }
    }

    public static void validatingAmountBreakdown(String label, String expected, String actual) {
        if (expected.equals(actual)) {
            logger.debug("[RecvInv] Amount matched - {}: {}", label, actual);
        } else {
            logger.warn("[RecvInv] Amount mismatch - {} | Expected: {}, Found: {}", label, expected, actual);
        }
    }

    public static void submittingReceiveInvoice() {
        logger.info("[RecvInv] Submitting Receive Invoice...");
    }

    public static void receiveInvoiceSuccess(String invoiceRefNo) {
        logger.info(PurchaseLogs.Invoice.created(invoiceRefNo));
    }

    public static void errorDuringReceiveInvoice(String step, Throwable t) {
        logger.error(PurchaseLogs.Invoice.error(step, t));
    }

    public static void info(String message) {
        logger.info("[RecvInv] {}", message);
    }

    public static void warn(String message) {
        logger.warn("[RecvInv] {}", message);
    }

    public static void error(String message, Throwable t) {
        logger.error("[RecvInv] {}", message, t);
    }
}