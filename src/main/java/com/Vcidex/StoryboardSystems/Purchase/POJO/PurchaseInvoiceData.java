/**
 * POJO (Plain Old Java Object) that holds all field values needed
 * to create a Purchase Invoice in the SBS ERP System.
 *
 * This includes invoice metadata, line items, amounts, taxes, and extra charges.
 * This class is used both in UI automation and test data factories.
 */
package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PurchaseInvoiceData {

    // ─── Basic Invoice Details ──────────────────────────────────────
    private String branchName;
    private String invoiceRefNo;
    private LocalDate invoiceDate;
    private LocalDate dueDate;

    // ─── Vendor / Customer Information ───────────────────────────────
    private String vendorName;
    private String vendorDetails;
    private String billTo;
    private String shipTo;
    private String requestedBy;
    private String requestorContactDetails;

    // ─── Transactional Terms ────────────────────────────────────────
    private String deliveryTerms;
    private String paymentTerms;
    private String purchaseType;
    private String dispatchMode;
    private String currency;
    private BigDecimal exchangeRate;
    private String coverNote;
    private LocalDate renewalDate;
    private String frequency;
    private String termsAndConditions;
    private String termsEditorText;

    // ─── Line Items and Financials ──────────────────────────────────
    @Builder.Default private List<LineItem> lineItems = java.util.Collections.emptyList();

    @Builder.Default private BigDecimal netAmount = BigDecimal.ZERO;
    @Builder.Default private BigDecimal addOnCharges = BigDecimal.ZERO;
    @Builder.Default private BigDecimal additionalDiscount = BigDecimal.ZERO;
    @Builder.Default private BigDecimal freightCharges = BigDecimal.ZERO;
    @Builder.Default private BigDecimal roundOff = BigDecimal.ZERO;

    @Builder.Default private boolean renewal = false;
    @Builder.Default private String additionalTax = "0%";
    @Builder.Default private BigDecimal grandTotal = BigDecimal.ZERO;



    // ─── Misc Fields ────────────────────────────────────────────────
    private String remarks;
    private String billingEmail;
    private String termsTemplate;
    private String termsContent;

    // ────────────────────────────────────────────────────────────────

    /**
     * Calculates the sum of all line item total amounts.
     * This method must be called before computing final total.
     */
    public void computeNetAmount() {
        if (lineItems == null) { lineItems = java.util.Collections.emptyList(); }
        this.netAmount = lineItems.stream()
                .map(li -> {
                    // prefer already-computed total; else compute now
                    return li.getTotalAmount() != null ? li.getTotalAmount() : li.computeAndGetTotal();
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Computes the final grand total by applying:
     *  - Add-ons (charges like freight, packing)
     *  - Subtracting additional discount
     *  - Adding any extra tax as percentage
     *  - Rounding the final amount
     */
    public void computeGrandTotal() {
        if (netAmount == null) netAmount = BigDecimal.ZERO;

        BigDecimal interim = netAmount
                .add(addOnCharges != null ? addOnCharges : BigDecimal.ZERO)
                .subtract(additionalDiscount != null ? additionalDiscount : BigDecimal.ZERO)
                .add(freightCharges != null ? freightCharges : BigDecimal.ZERO);

        BigDecimal pct = BigDecimal.ZERO;
        if (additionalTax != null) {
            String onlyNum = additionalTax.replaceAll("[^\\d.\\-]", "");
            if (!onlyNum.isBlank()) {
                try { pct = new BigDecimal(onlyNum).movePointLeft(2); } catch (Exception ignored) {}
            }
        }

        BigDecimal taxAmt = interim.multiply(pct).setScale(2, RoundingMode.HALF_UP);
        BigDecimal raw    = interim.add(taxAmt);

        BigDecimal rounded = raw.setScale(0, RoundingMode.HALF_UP);
        this.roundOff  = rounded.subtract(raw);
        this.grandTotal = raw.add(roundOff);
    }
}