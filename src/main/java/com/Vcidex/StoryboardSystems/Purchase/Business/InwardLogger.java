// InwardLogger.java
package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InwardLogger {
    private static final Logger logger = LoggerFactory.getLogger("InwardBusiness");
    private static final ThreadLocal<String> INWARD_ID = new ThreadLocal<>();

    public static void startProcess() {
        String temp = "INW-temp-" + Thread.currentThread().getId();
        INWARD_ID.set(temp);
        logger.info("[Inward:{}] Started Material Inward creation.", temp);
    }

    public static void addedDetails(String deliveryNo) {
        logger.info("[Inward:{}] {}", INWARD_ID.get(), PurchaseLogs.Inward.details(deliveryNo));
    }

    public static void addedProducts(int count) {
        logger.info("[Inward:{}] {}", INWARD_ID.get(), PurchaseLogs.Inward.addedProducts(count));
    }

    public static void submitting() {
        logger.info("[Inward:{}] {}", INWARD_ID.get(), PurchaseLogs.Inward.submitted());
    }

    public static void summaryValidated(String inwardNo) {
        INWARD_ID.set(inwardNo);
        logger.info("[Inward:{}] {}", inwardNo, PurchaseLogs.Inward.summaryValidated(inwardNo));
    }

    public static void completed() {
        logger.info("[Inward:{}] {}", INWARD_ID.get(), PurchaseLogs.Inward.completed());
    }

    /** Business metric hook */
    public static void metricLines(int count) {
        logger.info("[Inward:{}][METRIC] InwardLines count={}", INWARD_ID.get(), count);
    }
}