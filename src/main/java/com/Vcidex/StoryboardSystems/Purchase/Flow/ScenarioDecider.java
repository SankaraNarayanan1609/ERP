package com.Vcidex.StoryboardSystems.Purchase.Flow;

import com.Vcidex.StoryboardSystems.Purchase.Model.EntryType;
import com.Vcidex.StoryboardSystems.Purchase.Model.PaymentType;
import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;

public class ScenarioDecider {
    public static PurchaseScenario fromData(PurchaseOrderData d) {
        boolean hasGRN = d.getProductType()== ProductType.PHYSICAL
                && (d.getEntryType()== EntryType.PI_PO
                || d.getEntryType()==EntryType.DIRECT_PO);

        boolean hasReturns = hasGRN && d.getRejectCount()>0;
        boolean isForex    = !"INR".equals(d.getVendorCurrency());

        PaymentType pt = switch (d.getPaymentStyle()) {
            case FULL    -> PaymentType.FULL;
            case PARTIAL -> PaymentType.PARTIAL;
            case MULTI   -> PaymentType.MULTI;
        };

        return new PurchaseScenario(
                d.getEntryType(),
                d.getProductType(),
                d.getRejectCount(),
                hasGRN,
                hasReturns,
                pt,
                isForex
        );
    }
}
