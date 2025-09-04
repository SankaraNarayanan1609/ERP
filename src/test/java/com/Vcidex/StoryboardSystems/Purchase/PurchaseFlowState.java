package com.Vcidex.StoryboardSystems.Purchase;

/** Minimal per-test state; holds system-generated PO reference. */
public final class PurchaseFlowState {
    private String poRef;
    private String invoiceRef;
    private String grnRef;
    private String paymentRef;

    public String getPoRef() { return poRef; }
    public void setPoRef(String poRef) { this.poRef = poRef; }

    public String getInvoiceRef() { return invoiceRef; }
    public void setInvoiceRef(String invoiceRef) { this.invoiceRef = invoiceRef; }

    public String getGrnRef() { return grnRef; }
    public void setGrnRef(String grnRef) { this.grnRef = grnRef; }

    public String getPaymentRef() { return paymentRef; }
    public void setPaymentRef(String paymentRef) { this.paymentRef = paymentRef; }
}