package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class PurchaseObserver implements Observer {
    private static final Logger logger = LoggerFactory.getLogger(PurchaseObserver.class);

    @Override
    public void update(String poRefNo, String status, String pageName) {
        if (pageName.equalsIgnoreCase("Purchase Order Page")) {
            logger.info("[PURCHASE] [{}] PO Ref No: {}, Status: {}", LocalDateTime.now(), poRefNo, status);
        }
    }
}


