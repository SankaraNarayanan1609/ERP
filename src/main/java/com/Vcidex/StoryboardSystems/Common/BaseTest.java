package com.Vcidex.StoryboardSystems.Common;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseObserver;
import com.Vcidex.StoryboardSystems.Inventory.InventoryObserver;

public class BaseTest {
    public static void main(String[] args) {
        Subject subject = new Subject();

        // Register observers
        Observer purchaseObserver = new PurchaseObserver();
        Observer inventoryObserver = new InventoryObserver();

        subject.registerObserver(purchaseObserver);
        subject.registerObserver(inventoryObserver);

        // Simulate state changes
        subject.setState("PO12345", "Approved", "Purchase Order Page");
        subject.setState("PO12345", "Received", "Goods Receipt Note Page");
    }
}