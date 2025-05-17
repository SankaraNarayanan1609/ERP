// InwardQCLogger.java
package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InwardQCLogger {
    private static final Logger logger = LoggerFactory.getLogger("InwardQCBusiness");
    private static final ThreadLocal<String> INWARD_ID = new ThreadLocal<>();

    /** Call _after_ Inward summaryValidated(...) */
    public static void startQC(String inwardNo) {
        INWARD_ID.set(inwardNo);
        logger.info("[QC:{}] Started Inward QC for received goods.", inwardNo);
    }

    public static void fillDetails(String deliveryNo, String vendor) {
        logger.info("[QC:{}] {}", INWARD_ID.get(),
                PurchaseLogs.QC.details(deliveryNo, vendor));
    }

    public static void productCheck(String group, String name) {
        logger.info("[QC:{}] {}", INWARD_ID.get(),
                PurchaseLogs.QC.product(group, name));
    }

    public static void quantityVerification(String inwardQty, String deliveredQty) {
        logger.info("[QC:{}] {}", INWARD_ID.get(),
                PurchaseLogs.QC.quantity(inwardQty, deliveredQty));
    }

    public static void rejectedQuant(String qty) {
        logger.warn("[QC:{}] {}", INWARD_ID.get(),
                PurchaseLogs.QC.rejected(qty));
    }

    public static void shortageQuant(String qty) {
        logger.info("[QC:{}] {}", INWARD_ID.get(),
                PurchaseLogs.QC.shortage(qty));
    }

    public static void remarks(String remarks) {
        logger.info("[QC:{}] {}", INWARD_ID.get(),
                PurchaseLogs.QC.remarks(remarks));
    }

    public static void submitQC() {
        logger.info("[QC:{}] {}", INWARD_ID.get(), PurchaseLogs.QC.submitted());
    }

    public static void completeQC(String deliveryNo) {
        logger.info("[QC:{}] {}", INWARD_ID.get(),
                PurchaseLogs.QC.completed(deliveryNo));
    }

    /** Metric hook for QC defects */
    public static void metricDefects(int count) {
        logger.info("[QC:{}][METRIC] Defects count={}", INWARD_ID.get(), count);
    }
}