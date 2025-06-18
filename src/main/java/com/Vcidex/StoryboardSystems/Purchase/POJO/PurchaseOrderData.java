/**
 * ─────────────────────────────────────────────────────────────────────────────
 * POJO: PurchaseOrderData
 * Represents the full set of data required to fill and submit a Direct Purchase Order (PO).
 *
 * 🧱 Structured Into:
 *  - PO Header Info (branch, vendor, address, etc.)
 *  - Renewal Info (AMC-related fields)
 *  - Terms & Conditions
 *  - Line Items (products/services being purchased)
 *  - Financial Summary (net, charges, discount, tax, total)
 *
 * 🔄 Lifecycle:
 *  - Filled by factory (PurchaseOrderDataFactory)
 *  - Consumed by Page Object (DirectPO.java)
 *  - Verified post-submission in validations
 */

package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.math.BigDecimal;       // For monetary values
import java.math.RoundingMode;     // For rounding logic in tax/grand total
import java.time.LocalDate;        // Represents dates like PO date, expected date
import java.util.List;             // Used for holding multiple LineItems

import lombok.Builder;             // Lombok: Auto-generates builder pattern for this POJO
import lombok.Data;                // Lombok: Auto-generates getters, setters, toString, equals, hashCode

/**
 * ─────────────────────────────────────────────────────────────────────────────
 * Lombok Annotations:
 * - {@code @Data}    → Generates getters, setters, equals, hashCode, toString
 * - {@code @Builder} → Enables chained object creation using builder pattern
 *                      Useful in Factory class when setting random/test data
 *
 * Example:
 * PurchaseOrderData data = PurchaseOrderData.builder()
 *      .branchName("Chennai")
 *      .poRefNo("PO-123456")
 *      .poDate(LocalDate.now())
 *      .lineItems(List.of(...))
 *      .build();
 * ─────────────────────────────────────────────────────────────────────────────
 */
@Data
@Builder(toBuilder = true)
public class PurchaseOrderData {

    // ─── PO Header Fields ─────────────────────────────
    // Core information like branch, vendor, and contact
    private String branchName;
    private String poRefNo;
    private LocalDate poDate;
    private LocalDate expectedDate;
    private String vendorName;
    private String vendorDetails;
    private String billTo;
    private String shipTo;
    private String requestedBy;
    private String requestorContactDetails;
    private String deliveryTerms;
    private String paymentTerms;
    private String dispatchMode;
    private String currency;
    private BigDecimal exchangeRate;
    private String coverNote;

    // ─── Renewal Settings ─────────────────────────────
    // Optional fields used for AMC or subscription flows
    private boolean renewal;
    private LocalDate renewalDate;
    private String frequency;

    // ─── Terms and Conditions ────────────────────────────────────────────────
    private String termsAndConditions;
    private String termsEditorText;

    // ─── Product Line Items ──────────────────────────────────────────────────
    private List<LineItem> lineItems;

    // ─── Financial Summary Fields ────────────────────────────────────────────
    private BigDecimal netAmount;
    private BigDecimal addOnCharges;
    private BigDecimal additionalDiscount;
    private BigDecimal freightCharges;
    private String additionalTax;

    // ─── Financial Totals ─────────────────────────────
    // Auto-calculated or derived summary values
    private BigDecimal roundOff;
    private BigDecimal grandTotal;

    // ─── Calculation Methods ─────────────────────────────────────────────────

    /**
     * Calculates the net amount by summing totalAmount of all line items.
     * This is typically:
     * (Price × Qty − Discount + Tax) per row → summed
     */
    public void computeNetAmount() {
        this.netAmount = lineItems.stream()
                .map(LineItem::getTotalAmount)     // Get total per item
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Sum across all items
    }

    /**
     * Accessor for line items (used by Page classes or tests)
     * @return List of LineItem entries for this PO
     */
    public List<LineItem> getLineItems() {
        return this.lineItems;
    }

    /**
     * Computes the final grand total:
     * Formula: 
     *     netAmount 
     *   + addOnCharges 
     *   − additionalDiscount 
     *   + freightCharges 
     *   + additionalTax (as %)
     *   ± roundOff (auto-adjust to nearest integer)
     */
    public void computeGrandTotal() {
        if (netAmount == null) netAmount = BigDecimal.ZERO;

        // Step 1: Add base + charges − discount
        BigDecimal interim = netAmount
                .add(addOnCharges != null ? addOnCharges : BigDecimal.ZERO)
                .subtract(additionalDiscount != null ? additionalDiscount : BigDecimal.ZERO)
                .add(freightCharges != null ? freightCharges : BigDecimal.ZERO);

        // Step 2: Parse tax percentage string to BigDecimal
        BigDecimal pct = BigDecimal.ZERO;
        if (additionalTax != null && additionalTax.contains("%")) {
            try {
                String num = additionalTax.replace("%", "").trim();
                pct = new BigDecimal(num)
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                // Invalid % input: fallback to 0
            }
        }

        // Step 3: Add tax and round off
        BigDecimal taxAmt   = interim.multiply(pct).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rawTotal = interim.add(taxAmt);

        // Step 4: Round to nearest integer and apply roundOff difference
        BigDecimal rounded  = rawTotal.setScale(0, RoundingMode.HALF_UP);
        this.roundOff       = rounded.subtract(rawTotal);
        this.grandTotal     = rawTotal.add(roundOff);
    }
}