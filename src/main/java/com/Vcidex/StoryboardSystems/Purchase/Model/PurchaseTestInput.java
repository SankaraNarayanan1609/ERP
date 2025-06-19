package com.Vcidex.StoryboardSystems.Purchase.Model;

public class PurchaseTestInput {
    private final ProductType productType;
    private final EntryType entryType;
    private final String currency;

    public PurchaseTestInput(ProductType productType, EntryType entryType, String currency) {
        this.productType = productType;
        this.entryType = entryType;
        this.currency = currency;
    }

    public ProductType getProductType() {
        return productType;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return productType + " | " + entryType + " | " + currency;
    }
}