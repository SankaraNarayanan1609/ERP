// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/Model/FlowSpec.java
package com.Vcidex.StoryboardSystems.Purchase.Model;

public record FlowSpec(FlowKind flow, ProductType productType, CurrencyKind currency) {
    public boolean needsIndent()  { return flow == FlowKind.PI_BASED_PO; }
    public boolean needsPO()      { return flow != FlowKind.DIRECT_INVOICE; }
    public boolean needsInward()  { return needsPO() && productType == ProductType.PHYSICAL; }
    public boolean needsInvoice() { return true; }
    public boolean needsPayment() { return true; }

    @Override public String toString() {
        String p = (flow == FlowKind.DIRECT_INVOICE) ? "-" : productType.name();
        return flow + " | " + p + " | " + currency;
    }
}