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
    private boolean renewal;
    private LocalDate renewalDate;
    private String frequency;
    private String termsAndConditions;
    private String termsEditorText;

    // ─── Line Items and Financials ──────────────────────────────────
    private List<LineItem> lineItems;
    private BigDecimal netAmount;
    private BigDecimal addOnCharges;
    private BigDecimal additionalDiscount;
    private BigDecimal freightCharges;
    private String additionalTax;
    private BigDecimal roundOff;
    private BigDecimal grandTotal;

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
        this.netAmount = lineItems.stream()
                .map(LineItem::getTotalAmount) // get the total (price * qty - discount + tax)
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

        // Step 1: Add or subtract additional charges
        BigDecimal interim = netAmount
                .add(addOnCharges != null ? addOnCharges : BigDecimal.ZERO)
                .subtract(additionalDiscount != null ? additionalDiscount : BigDecimal.ZERO)
                .add(freightCharges != null ? freightCharges : BigDecimal.ZERO);

        // Step 2: Parse additional tax percentage string (like "5%")
        BigDecimal pct = BigDecimal.ZERO;
        if (additionalTax != null && additionalTax.contains("%")) {
            try {
                String num = additionalTax.replace("%", "").trim(); // remove %
                pct = new BigDecimal(num)
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                // If parsing fails, assume 0%
            }
        }

        // Step 3: Calculate tax amount and final total
        BigDecimal taxAmt = interim.multiply(pct).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rawTotal = interim.add(taxAmt);

        // Step 4: Round the total and save rounding difference
        BigDecimal rounded = rawTotal.setScale(0, RoundingMode.HALF_UP);
        this.roundOff = rounded.subtract(rawTotal);
        this.grandTotal = rawTotal.add(roundOff);
    }
}