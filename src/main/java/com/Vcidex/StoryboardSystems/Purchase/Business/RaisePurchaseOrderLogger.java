package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RaisePurchaseOrderLogger {

    private static final Logger logger = LoggerFactory.getLogger("RaisePurchaseOrderBusiness");

    public static void startPOCreation(String vendorName, String sourceInvoice) {
        logger.info("[RaisePO] Starting Raise PO creation for Vendor: {} | From Invoice: {}", vendorName, sourceInvoice);
    }

    public static void addedPODetails(String deliveryNo) {
        logger.info(PurchaseLogs.Inward.details(deliveryNo));
    }

    public static void selectedProduct(String productName) {
        logger.debug("[RaisePO] Selected product: {}", productName);
    }

    public static void enteredProductPrice(String productName, double price) {
        logger.debug("[RaisePO] Entered price for product [{}]: ₹{}", productName, price);
    }

    public static void validateProductQuantity(String productName, int quantityPI, int quantityPO) {
        if (quantityPI == quantityPO) {
            logger.debug("[RaisePO] Quantity matched for [{}] | Quantity: {}", productName, quantityPO);
        } else {
            logger.warn("[RaisePO] Quantity mismatch for [{}] | PI: {}, PO: {}", productName, quantityPI, quantityPO);
        }
    }

    public static void validateTotalAmount(double expectedTotal, double actualTotal) {
        if (Double.compare(expectedTotal, actualTotal) == 0) {
            logger.debug("[RaisePO] Total PO amount is correct: ₹{}", actualTotal);
        } else {
            logger.warn("[RaisePO] Total PO amount mismatch | Expected: ₹{}, Actual: ₹{}", expectedTotal, actualTotal);
        }
    }

    public static void addedGeneralDetails(String poType, String deliveryLocation) {
        logger.debug("[RaisePO] Entered general details - PO Type: {}, Delivery Location: {}", poType, deliveryLocation);
    }

    public static void selectedMultipleProducts(int count) {
        logger.debug("[RaisePO] Selected {} products in total.", count);
    }

    public static void submittingPO() {
        logger.info("[RaisePO] Submitting the Purchase Order form...");
    }

    public static void poCreationSuccess(String poNumber, String vendor) {
        logger.info(PurchaseLogs.PO.created(poNumber, vendor));
    }

    public static void poApprovalSuccess(String poNumber, String approver) {
        logger.info(PurchaseLogs.PO.approved(poNumber, approver));
    }

    public static void poCancelled(String poNumber, String reason) {
        logger.warn(PurchaseLogs.PO.cancelled(poNumber, reason));
    }

    public static void errorDuringPOCreation(String poNumber, String step, Throwable t) {
        logger.error(PurchaseLogs.PO.error(poNumber, step), t);
    }
}
