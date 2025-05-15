package com.Vcidex.StoryboardSystems.Purchase.Business;

import com.Vcidex.StoryboardSystems.LogMessages.PurchaseLogs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InwardLogger {

    private static final Logger logger = LoggerFactory.getLogger(InwardLogger.class);

    public static void startInwardProcess() {
        logger.info(PurchaseLogs.Inward.started());
    }

    public static void addedInwardDetails(String deliveryNo) {
        logger.info(PurchaseLogs.Inward.details(deliveryNo));
    }

    public static void addedProducts(int productCount) {
        logger.info(PurchaseLogs.Inward.addedProducts(productCount));
    }

    public static void submittedInward() {
        logger.info(PurchaseLogs.Inward.submitted());
    }

    public static void validatedInwardSummary(String inwardNumber) {
        logger.info(PurchaseLogs.Inward.summaryValidated(inwardNumber));
    }

    public static void completedInwardProcess() {
        logger.info(PurchaseLogs.Inward.completed());
    }
}