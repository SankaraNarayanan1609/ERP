package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.Observer;

public class PurchaseObserver implements Observer {
    @Override
    public void update(String poRefNo, String status, String pageName) {
        if (pageName.equalsIgnoreCase("Purchase Order Page")) {
            System.out.println("[PurchaseObserver] Observing PO Ref No: " + poRefNo + ", Status: " + status);
            // Add logic for handling updates in Purchase
        }
    }
}//

