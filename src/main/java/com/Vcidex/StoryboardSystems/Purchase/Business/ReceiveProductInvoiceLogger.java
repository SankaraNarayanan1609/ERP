package com.Vcidex.StoryboardSystems.Purchase.Business;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;

public class ReceiveProductInvoiceLogger {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveProductInvoiceLogger.class);

    public static void startValidation(String invoiceRefNo) {
        logger.info(PurchaseLogs.Invoice.created(invoiceRefNo));
    }

    public static void validatingField(String fieldName, String expected, String actual) {
        if (expected.equals(actual)) {
            logger.debug("[ReceiveProductInvoice] Field matched - {}: {}", fieldName, actual);
        } else {
            logger.warn(PurchaseLogs.Invoice.error(fieldName,
                    new Throwable(String.format("Expected: %s, Found: %s", expected, actual))));
        }
    }

    public static void validatingProductLineItem(String productCode, int poQty, int grnQty, int invoiceQty,
                                                 double poPrice, double invoicePrice) {
        logger.debug("[ReceiveProductInvoice] Validating Product - Code: {}", productCode);

        if (grnQty == invoiceQty && poPrice == invoicePrice) {
            logger.debug("[ReceiveProductInvoice] Product OK - GRN Qty: {}, Inv Qty: {}, Price: {}", grnQty, invoiceQty, invoicePrice);
        } else {
            logger.warn("[ReceiveProductInvoice] Product mismatch for {} - GRN Qty: {}, Invoice Qty: {}, Price: {} (Expected: {})",
                    productCode, grnQty, invoiceQty, invoicePrice, poPrice);
        }
    }

    public static void validatingAmount(String label, double orderAmount, double invoiceAmount) {
        if (Double.compare(orderAmount, invoiceAmount) == 0) {
            logger.debug("[ReceiveProductInvoice] Amount matched - {}: {}", label, invoiceAmount);
        } else {
            logger.warn("[ReceiveProductInvoice] Amount mismatch - {} | Order: {}, Invoice: {}", label, orderAmount, invoiceAmount);
        }
    }

    public static void submittingInvoice() {
        logger.info("[ReceiveProductInvoice] Submitting Purchase Invoice...");
    }

    public static void invoiceSubmissionSuccess(String refNo) {
        logger.info(PurchaseLogs.Invoice.created(refNo));
    }

    public static void error(String step, Throwable t) {
        logger.error(PurchaseLogs.Invoice.error(step, t));
    }

    public static void info(String message) {
        logger.info("[ReceiveProductInvoice] {}", message);
    }

    public static void warn(String message) {
        logger.warn("[ReceiveProductInvoice] {}", message);
    }
}