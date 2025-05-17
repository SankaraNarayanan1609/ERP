// RaisePurchaseOrderLogger.java
package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaisePurchaseOrderLogger {
    private static final Logger logger = LoggerFactory.getLogger("RaisePurchaseOrderBusiness");
    private static final ThreadLocal<String> RPO_ID = new ThreadLocal<>();

    public static void start(String vendor, String srcInv) {
        String temp = "RPO-temp-" + Thread.currentThread().getId();
        RPO_ID.set(temp);
        logger.info("[RaisePO:{}] Starting Raise PO for Vendor: {} | From Invoice: {}",
                temp, vendor, srcInv);
    }

    public static void addedDetails(String deliveryNo) {
        logger.info("[RaisePO:{}] {}", RPO_ID.get(),
                PurchaseLogs.Inward.details(deliveryNo));
    }

    public static void selectedProduct(String name) {
        logger.debug("[RaisePO:{}] Selected product: {}", RPO_ID.get(), name);
    }

    public static void enteredPrice(String name, double price) {
        logger.debug("[RaisePO:{}] Entered price for {}: ₹{}", RPO_ID.get(), name, price);
    }

    public static void validateQuantity(String name, int piQty, int poQty) {
        if (piQty == poQty) {
            logger.debug("[RaisePO:{}] Quantity matched for {}: {}", RPO_ID.get(), name, poQty);
        } else {
            logger.warn("[RaisePO:{}] Quantity mismatch for {}: PI={} PO={}",
                    RPO_ID.get(), name, piQty, poQty);
        }
    }

    public static void validateTotal(double exp, double act) {
        if (Double.compare(exp, act)==0) {
            logger.debug("[RaisePO:{}] Total PO amount: ₹{}", RPO_ID.get(), act);
        } else {
            logger.warn("[RaisePO:{}] Total amount mismatch – expected=₹{} actual=₹{}",
                    RPO_ID.get(), exp, act);
        }
    }

    public static void addedGeneralDetails(String type, String loc) {
        logger.debug("[RaisePO:{}] General details – Type: {}, Location: {}", RPO_ID.get(), type, loc);
    }

    public static void submitting() {
        logger.info("[RaisePO:{}] Submitting Raise PO form...", RPO_ID.get());
    }

    public static void creationSuccess(String poNumber, String vendor) {
        RPO_ID.set(poNumber);
        logger.info("[RaisePO:{}] {}", poNumber, PurchaseLogs.PO.created(poNumber, vendor));
    }

    public static void approvalSuccess(String poNumber, String approver) {
        logger.info("[RaisePO:{}] {}", RPO_ID.get(), PurchaseLogs.PO.approved(poNumber, approver));
    }

    public static void cancelled(String poNumber, String reason) {
        logger.warn("[RaisePO:{}] {}", RPO_ID.get(), PurchaseLogs.PO.cancelled(poNumber, reason));
    }

    public static void error(String step, Throwable t) {
        logger.error("[RaisePO:{}] {}", RPO_ID.get(), PurchaseLogs.PO.error(RPO_ID.get(), step), t);
    }

    public static void metricLines(int count) {
        logger.info("[RaisePO:{}][METRIC] RaisePOLines count={}", RPO_ID.get(), count);
    }
}