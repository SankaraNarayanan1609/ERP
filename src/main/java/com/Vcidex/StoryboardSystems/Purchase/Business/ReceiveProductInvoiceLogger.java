// ReceiveProductInvoiceLogger.java
package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveProductInvoiceLogger {
    private static final Logger logger = LoggerFactory.getLogger("ReceiveProductInvoiceBusiness");
    private static final ThreadLocal<String> INV_ID = new ThreadLocal<>();

    public static void start(String invoiceRefNo) {
        INV_ID.set(invoiceRefNo);
        logger.info("[RecvProdInv:{}] Validating Invoice: {}", invoiceRefNo, invoiceRefNo);
    }

    public static void validatingField(String field, String exp, String act) {
        if (exp.equals(act)) {
            logger.debug("[RecvProdInv:{}] Field matched – {}: {}", INV_ID.get(), field, act);
        } else {
            logger.warn("[RecvProdInv:{}] Field mismatch – {} expected='{}' found='{}'",
                    INV_ID.get(), field, exp, act);
        }
    }

    public static void validatingLineItem(String code,
                                          int poQty, int grnQty, int invQty,
                                          double poPrice, double invPrice) {
        logger.debug("[RecvProdInv:{}] Validating product {}", INV_ID.get(), code);
        if (grnQty==invQty && Double.compare(poPrice, invPrice)==0) {
            logger.debug("[RecvProdInv:{}] Product OK – GRN={}, InvQty={}, Price={}",
                    INV_ID.get(), grnQty, invQty, invPrice);
        } else {
            logger.warn("[RecvProdInv:{}] Product mismatch {} – GRN={}, InvQty={}, InvPrice={} (Expected Price={})",
                    INV_ID.get(), code, grnQty, invQty, invPrice, poPrice);
        }
    }

    public static void validatingAmount(String label, double orderAmt, double invAmt) {
        if (Double.compare(orderAmt, invAmt)==0) {
            logger.debug("[RecvProdInv:{}] Amount matched – {}: {}", INV_ID.get(), label, invAmt);
        } else {
            logger.warn("[RecvProdInv:{}] Amount mismatch – {} order={} inv={}",
                    INV_ID.get(), label, orderAmt, invAmt);
        }
    }

    public static void submitInvoice() {
        logger.info("[RecvProdInv:{}] Submitting Purchase Invoice...", INV_ID.get());
    }

    public static void submissionSuccess(String refNo) {
        logger.info("[RecvProdInv:{}] {}", refNo, PurchaseLogs.Invoice.created(refNo));
    }

    public static void error(String step, Throwable t) {
        logger.error("[RecvProdInv:{}] {}", INV_ID.get(), PurchaseLogs.Invoice.error(step, t), t);
    }
}