// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/POJO/PurchaseOrderData.java
package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class PurchaseOrderData {
    // ──────────────────────────────────────────────────────────────────────────
    // Fields
    // ──────────────────────────────────────────────────────────────────────────
    private String           branchName;
    private String           poRefNo;
    private LocalDate        poDate;
    private LocalDate        expectedDate;
    private String           vendorName;
    private String           vendorDetails;
    private String           billTo;
    private String           shipTo;
    private String           requestedBy;
    private String           requestorContactDetails;
    private String           deliveryTerms;
    private String           paymentTerms;
    private String           dispatchMode;
    private String           currency;
    private BigDecimal       exchangeRate;
    private String           coverNote;
    private boolean          renewal;
    private LocalDate        renewalDate;
    private String           frequency;
    private List<LineItem>   lineItems;
    private BigDecimal       netAmount;
    private BigDecimal       addOnCharges;
    private BigDecimal       additionalDiscount;
    private BigDecimal       freightCharges;
    private String           additionalTax;
    private BigDecimal       roundOff;
    private BigDecimal       grandTotal;
    private String           termsAndConditions;
    private String           termsEditorText;

    // Private constructor
    private PurchaseOrderData() {}

    // ──────────────────────────────────────────────────────────────────────────
    // Builder
    // ──────────────────────────────────────────────────────────────────────────
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PurchaseOrderData d = new PurchaseOrderData();

        public Builder branchName(String b)                  { d.branchName = b; return this; }
        public Builder poRefNo(String ref)                   { d.poRefNo = ref; return this; }
        public Builder poDate(LocalDate date)                { d.poDate = date; return this; }
        public Builder expectedDate(LocalDate date)          { d.expectedDate = date; return this; }
        public Builder vendorName(String v)                  { d.vendorName = v; return this; }
        public Builder vendorDetails(String vd)              { d.vendorDetails = vd; return this; }
        public Builder billTo(String bt)                     { d.billTo = bt; return this; }
        public Builder shipTo(String st)                     { d.shipTo = st; return this; }
        public Builder requestedBy(String r)                 { d.requestedBy = r; return this; }
        public Builder requestorContactDetails(String rc)    { d.requestorContactDetails = rc; return this; }
        public Builder deliveryTerms(String dt)              { d.deliveryTerms = dt; return this; }
        public Builder paymentTerms(String pt)               { d.paymentTerms = pt; return this; }
        public Builder dispatchMode(String dm)               { d.dispatchMode = dm; return this; }
        public Builder currency(String c)                    { d.currency = c; return this; }
        public Builder exchangeRate(BigDecimal er)           { d.exchangeRate = er; return this; }
        public Builder coverNote(String cn)                  { d.coverNote = cn; return this; }
        public Builder renewal(boolean r)                    { d.renewal = r; return this; }
        public Builder renewalDate(LocalDate rd)             { d.renewalDate = rd; return this; }
        public Builder frequency(String f)                   { d.frequency = f; return this; }
        public Builder lineItems(List<LineItem> items)       { d.lineItems = items; return this; }
        public Builder netAmount(BigDecimal na)              { d.netAmount = na; return this; }
        public Builder addOnCharges(BigDecimal aoc)          { d.addOnCharges = aoc; return this; }
        public Builder additionalDiscount(BigDecimal ad)     { d.additionalDiscount = ad; return this; }
        public Builder freightCharges(BigDecimal fc)         { d.freightCharges = fc; return this; }
        public Builder additionalTax(String at)              { d.additionalTax = at; return this; }
        public Builder roundOff(BigDecimal ro)               { d.roundOff = ro; return this; }
        public Builder grandTotal(BigDecimal gt)             { d.grandTotal = gt; return this; }
        public Builder termsAndConditions(String tc)         { d.termsAndConditions = tc; return this; }
        public Builder termsEditorText(String tet)           { d.termsEditorText = tet; return this; }

        public PurchaseOrderData build() {
            // Mandatory validations
            Objects.requireNonNull(d.branchName,    "branchName is required");
            Objects.requireNonNull(d.expectedDate,  "expectedDate is required");
            Objects.requireNonNull(d.vendorName,    "vendorName is required");
            Objects.requireNonNull(d.currency,      "currency is required");
            if (d.renewal) {
                Objects.requireNonNull(d.renewalDate, "renewalDate is required when renewal=true");
                Objects.requireNonNull(d.frequency,   "frequency is required when renewal=true");
            }
            return d;
        }
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Getters
    // ──────────────────────────────────────────────────────────────────────────
    public String getBranchName()             { return branchName; }
    public String getPoRefNo()                { return poRefNo; }
    public LocalDate getPoDate()              { return poDate; }
    public LocalDate getExpectedDate()        { return expectedDate; }
    public String getVendorName()             { return vendorName; }
    public String getVendorDetails()          { return vendorDetails; }
    public String getBillTo()                 { return billTo; }
    public String getShipTo()                 { return shipTo; }
    public String getRequestedBy()            { return requestedBy; }
    public String getRequestorContactDetails(){ return requestorContactDetails; }
    public String getDeliveryTerms()          { return deliveryTerms; }
    public String getPaymentTerms()           { return paymentTerms; }
    public String getDispatchMode()           { return dispatchMode; }
    public String getCurrency()               { return currency; }
    public BigDecimal getExchangeRate()       { return exchangeRate; }
    public String getCoverNote()              { return coverNote; }
    public boolean isRenewal()                { return renewal; }
    public LocalDate getRenewalDate()         { return renewalDate; }
    public String getFrequency()              { return frequency; }
    public List<LineItem> getLineItems()      { return lineItems; }
    public BigDecimal getNetAmount()          { return netAmount; }
    public BigDecimal getAddOnCharges()       { return addOnCharges; }
    public BigDecimal getAdditionalDiscount() { return additionalDiscount; }
    public BigDecimal getFreightCharges()     { return freightCharges; }
    public String getAdditionalTax()          { return additionalTax; }
    public BigDecimal getRoundOff()           { return roundOff; }
    public BigDecimal getGrandTotal()         { return grandTotal; }
    public String getTermsAndConditions()     { return termsAndConditions; }
    public String getTermsEditorText()        { return termsEditorText; }

    // ──────────────────────────────────────────────────────────────────────────
    // Calculation helpers (retain your existing implementations)
    // ──────────────────────────────────────────────────────────────────────────
    public void computeNetAmount() {
        this.netAmount = lineItems.stream()
                .map(LineItem::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void computeGrandTotal() {
        BigDecimal interim = netAmount
                .add(addOnCharges)
                .subtract(additionalDiscount)
                .add(freightCharges);
        BigDecimal taxAmt = interim.multiply(
                new BigDecimal(additionalTax.replace("%",""))
                        .divide(BigDecimal.valueOf(100))
        );
        BigDecimal rawTotal = interim.add(taxAmt);
        this.roundOff   = rawTotal.setScale(0, BigDecimal.ROUND_HALF_UP)
                .subtract(rawTotal);
        this.grandTotal = rawTotal.add(roundOff);
    }
}