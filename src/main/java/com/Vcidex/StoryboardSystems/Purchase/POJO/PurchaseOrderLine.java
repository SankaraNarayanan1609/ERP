// src/main/java/com/Vcidex/StoryboardSystems/Purchase/POJO/PurchaseOrderLine.java
package com.Vcidex.StoryboardSystems.Purchase.POJO;

public class PurchaseOrderLine {
    private final String productName;
    private final int orderedQty;

    public PurchaseOrderLine(String productName, int orderedQty) {
        this.productName = productName;
        this.orderedQty  = orderedQty;
    }

    public String getProductName() { return productName; }
    public int    getOrderedQty()  { return orderedQty;  }
}