package com.Vcidex.StoryboardSystems.Purchase.Model;

public record PurchaseTestInput(
        ProductType productType,
        EntryType   entryType,
        String      currency // e.g., "INR", "GBP"
) {
    @Override public String toString() {
        return productType + " | " + entryType + " | " + currency;
    }
}