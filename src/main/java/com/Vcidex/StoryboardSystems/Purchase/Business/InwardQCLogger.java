package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InwardQCLogger {

    private static final Logger logger = LoggerFactory.getLogger(InwardQCLogger.class);

    public static void startInwardQC() {
        logger.info(PurchaseLogs.QC.started());
    }

    public static void qcDetailsFilled(String deliveryNo, String vendorName) {
        logger.info(PurchaseLogs.QC.details(deliveryNo, vendorName));
    }

    public static void productQCCheck(String productGroup, String productName) {
        logger.info(PurchaseLogs.QC.product(productGroup, productName));
    }

    public static void quantityVerification(String inwardQty, String deliveredQty) {
        logger.info(PurchaseLogs.QC.quantity(inwardQty, deliveredQty));
    }

    public static void rejectedQtyLogged(String rejectedQty) {
        logger.warn(PurchaseLogs.QC.rejected(rejectedQty));
    }

    public static void shortageQtyLogged(String shortageQty) {
        logger.info(PurchaseLogs.QC.shortage(shortageQty));
    }

    public static void qcRemarksAdded(String remarks) {
        logger.info(PurchaseLogs.QC.remarks(remarks));
    }

    public static void submittedQCForm() {
        logger.info(PurchaseLogs.QC.submitted());
    }

    public static void completedQCValidation(String deliveryNo) {
        logger.info(PurchaseLogs.QC.completed(deliveryNo));
    }
}