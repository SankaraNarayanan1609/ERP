package com.Vcidex.StoryboardSystems.Purchase.Model;

public class PurchaseScenario {
    public final EntryType entryType;
    public final ProductType productType;
    public final int rejectCount;
    public final boolean hasGRN;
    public final boolean hasReturns;
    public final PaymentType paymentType;
    public final boolean isForex;

    public PurchaseScenario(EntryType entryType,
                            ProductType productType,
                            int rejectCount,
                            boolean hasGRN,
                            boolean hasReturns,
                            PaymentType paymentType,
                            boolean isForex) {
        this.entryType   = entryType;
        this.productType = productType;
        this.rejectCount = rejectCount;
        this.hasGRN      = hasGRN;
        this.hasReturns  = hasReturns;
        this.paymentType = paymentType;
        this.isForex     = isForex;
    }
}
