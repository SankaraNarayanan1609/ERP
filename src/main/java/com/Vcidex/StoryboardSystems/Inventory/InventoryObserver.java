package com.Vcidex.StoryboardSystems.Inventory;


import com.Vcidex.StoryboardSystems.Common.Observer;

public class InventoryObserver implements Observer {
    @Override
    public void update(String poRefNo, String status, String pageName) {
        if (pageName.equalsIgnoreCase("Goods Receipt Note Page")) {
            System.out.println("[InventoryObserver] Observing PO Ref No: " + poRefNo + ", Status: " + status);
            // Add logic for handling updates in Inventory
        }
    }
}