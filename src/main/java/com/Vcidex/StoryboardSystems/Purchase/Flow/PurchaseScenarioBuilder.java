// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Flow/PurchaseScenarioBuilder.java
package com.Vcidex.StoryboardSystems.Purchase.Flow;

import com.Vcidex.StoryboardSystems.Purchase.Model.*;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;

public class PurchaseScenarioBuilder {
    private EntryType    entryType;
    private ProductType  productType;
    private int          rejectCount;
    private boolean      hasGRN;
    private boolean      hasReturns;
    private PaymentType  paymentType;
    private boolean      isForex;

    /** Kick off the builder by pulling raw data */
    public PurchaseScenarioBuilder fromData(PurchaseOrderData d) {
        this.entryType   = d.getEntryType();
        this.productType = d.getProductType();
        this.rejectCount = d.getRejectCount();

        this.hasGRN      = computeHasGrn(d);
        this.hasReturns  = computeHasReturns(d);
        this.isForex     = computeIsForex(d);
        this.paymentType = computePaymentType(d);

        return this;
    }

    /*—— private helper methods, each its own “concern” ——*/
    private boolean computeHasGrn(PurchaseOrderData d) {
        return d.getProductType() == ProductType.PHYSICAL
                && (d.getEntryType() == EntryType.PI_PO
                || d.getEntryType() == EntryType.DIRECT_PO);
    }

    private boolean computeHasReturns(PurchaseOrderData d) {
        return computeHasGrn(d) && d.getRejectCount() > 0;
    }

    private boolean computeIsForex(PurchaseOrderData d) {
        return !"INR".equalsIgnoreCase(d.getVendorCurrency());
    }

    private PaymentType computePaymentType(PurchaseOrderData d) {
        return switch (d.getPaymentStyle()) {
            case FULL    -> PaymentType.FULL;
            case PARTIAL -> PaymentType.PARTIAL;
            case MULTI   -> PaymentType.MULTI;
        };
    }

    /** Final build method */
    public PurchaseScenario build() {
        return new PurchaseScenario(
                entryType,
                productType,
                rejectCount,
                hasGRN,
                hasReturns,
                paymentType,
                isForex
        );
    }
}