
/**
 * POJO representing the complete Purchase Order form structure used in automation.
 *
 * This class:
 * - Holds both header fields and line items
 * - Contains business logic to calculate net and grand totals
 * - Is used directly by Page classes like DirectPO
 * - Is populated dynamically using PurchaseOrderDataFactory
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

    // ─── PO Header Fields ────────────────────────────────────────────────────

    /** Branch under which the PO is being raised */
    private String branchName;

    /** PO Reference Number (unique) */
    private String poRefNo;

    /** Date when PO is created */
    private LocalDate poDate;

    /** Expected delivery date */
    private LocalDate expectedDate;

    /** Vendor's name */
    private String vendorName;

    /** Additional vendor details (may not be used) */
    private String vendorDetails;

    /** Billing address */
    private String billTo;

    /** Shipping address */
    private String shipTo;

    /** Name of the requestor employee */
    private String requestedBy;

    /** Contact details of the requestor */
    private String requestorContactDetails;

    /** Delivery terms entered by user */
    private String deliveryTerms;

    /** Payment terms agreed with vendor */
    private String paymentTerms;

    /** Dispatch mode like Courier, Email, etc. */
    private String dispatchMode;

    /** Currency used in PO (e.g., INR, USD) */
    private String currency;

    /** Exchange rate (if foreign currency is used) */
    private BigDecimal exchangeRate;

    /** Cover note written inside PO (HTML formatted in editor) */
    private String coverNote;

    // ─── Renewal (AMC/Contract-based) Fields ─────────────────────────────────

    /** Whether this PO is renewal-based (true = yes) */
    private boolean renewal;

    /** If renewal = true, this is the renewal date */
    private LocalDate renewalDate;

    /** Frequency of renewal (e.g., Monthly, Quarterly) */
    private String frequency;

    // ─── Terms and Conditions ────────────────────────────────────────────────

    /** Predefined terms or generated from backend */
    private String termsAndConditions;

    /** Editable rich-text terms written in Angular editor */
    private String termsEditorText;

    // ─── Product Line Items ──────────────────────────────────────────────────

    /** List of products/services being purchased */
    private List<LineItem> lineItems;

    // ─── Financial Summary Fields ────────────────────────────────────────────

    /** Sum of all product line totals */
    private BigDecimal netAmount;

    /** Any manual charges added (e.g., installation, packaging) */
    private BigDecimal addOnCharges;

    /** Extra discount given on total */
    private BigDecimal additionalDiscount;

    /** Freight or delivery charges added */
    private BigDecimal freightCharges;

    /** Additional tax percentage string (e.g., "18%") */
    private String additionalTax;

    /** Round-off value (auto calculated) */
    private BigDecimal roundOff;

    /** Final grand total after applying all logic */
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