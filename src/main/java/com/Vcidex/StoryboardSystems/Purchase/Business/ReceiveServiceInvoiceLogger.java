// ReceiveServiceInvoiceLogger.java
package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveServiceInvoiceLogger {
    private static final Logger logger = LoggerFactory.getLogger("ReceiveServiceInvoiceBusiness");
    private static final ThreadLocal<String> INV_ID = new ThreadLocal<>();

    public static void start(String invoiceRefNo) {
        INV_ID.set(invoiceRefNo);
        logger.info("[RecvSvcInv:{}] Validating Service Invoice: {}", invoiceRefNo, invoiceRefNo);
    }

    public static void validatingField(String field, String exp, String act) {
        if (exp.equals(act)) {
            logger.debug("[RecvSvcInv:{}] Field matched – {}: {}", INV_ID.get(), field, act);
        } else {
            logger.warn("[RecvSvcInv:{}] Field mismatch – {} expected='{}' found='{}'",
                    INV_ID.get(), field, exp, act);
        }
    }

    public static void validatingLineItem(String code, int expQty, int actQty, double expPrice, double actPrice) {
        logger.debug("[RecvSvcInv:{}] Validating service line {}", INV_ID.get(), code);
        if (expQty==actQty && Double.compare(expPrice, actPrice)==0) {
            logger.debug("[RecvSvcInv:{}] Line OK – Qty={}, Price={}", INV_ID.get(), actQty, actPrice);
        } else {
            logger.warn("[RecvSvcInv:{}] Line mismatch {} – Qty[exp={},act={}] Price[exp={},act={}]",
                    INV_ID.get(), code, expQty, actQty, expPrice, actPrice);
        }
    }

    public static void validatingAmountBreakdown(String label, String exp, String act) {
        if (exp.equals(act)) {
            logger.debug("[RecvSvcInv:{}] Amount matched – {}: {}", INV_ID.get(), label, act);
        } else {
            logger.warn("[RecvSvcInv:{}] Amount mismatch – {} expected='{}' found='{}'",
                    INV_ID.get(), label, exp, act);
        }
    }

    public static void submitReceive() {
        logger.info("[RecvSvcInv:{}] Submitting Receive Service Invoice...", INV_ID.get());
    }

    public static void success(String refNo) {
        logger.info("[RecvSvcInv:{}] {}", refNo, PurchaseLogs.Invoice.created(refNo));
    }

    public static void error(String step, Throwable t) {
        logger.error("[RecvSvcInv:{}] {}", INV_ID.get(), PurchaseLogs.Invoice.error(step, t), t);
    }
}