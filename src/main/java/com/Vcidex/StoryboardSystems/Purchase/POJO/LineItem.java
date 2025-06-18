/**
 * Represents a single product or service entry in a Purchase Order (PO).
 *
 * ─────────────────────────────────────────────────────────────────────────────
 * Lombok Annotations:
 * - {@code @Data}    → Auto-generates getters, setters, toString(), equals(), hashCode()
 * - {@code @Builder} → Enables the builder pattern to create instances fluently
 *
 * Example:
 * LineItem item = LineItem.builder()
 *                         .productName("UPS Battery")
 *                         .quantity(5)
 *                         .price(new BigDecimal("1200.00"))
 *                         .build();
 *
 *  * This saves a lot of boilerplate code, and should be used carefully for data-only classes.
 * ─────────────────────────────────────────────────────────────────────────────
 * Each LineItem holds:
 * - Product identity (group, code, name)
 * - Commercial values (price, quantity, discount, tax)
 * - Derived amount fields like total amount
 * - Embedded Product object for reference
 */
package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class LineItem {

    // ─── Product Identity ─────────────────────────────────────────────
    private String productGroup;   // e.g., "Electronics", "Raw Materials"
    private String productCode;    // Unique SKU or code
    private String productName;    // Human-readable name of product
    private String description;    // Optional remarks or extended name

    // ─── Commercial Fields ────────────────────────────────────────────
    private int quantity;          // Quantity ordered
    private BigDecimal price;      // Unit price
    private BigDecimal discountPct; // Optional discount % for display/reference
    private BigDecimal discountAmt; // Discount amount used for calculation

    // ─── Taxation Fields ──────────────────────────────────────────────
    private String taxPrefix;      // e.g., "GST 18%", for display
    private BigDecimal taxRate;    // e.g., 0.18 for 18% GST

    // ─── Derived Calculation ──────────────────────────────────────────
    private BigDecimal totalAmount; // Final line total after discount and tax

    // ─── Embedded Product Object ──────────────────────────────────────
    private Product product;       // Full product master object (optional, for traceability)

    /**
     * Returns the discount percentage if needed for reporting.
     * This is useful for summary displays or re-calculations.
     *
     * @return Discount percent applied on this line
     */
    public BigDecimal getDiscount() {
        return discountPct;
    }

    /**
     * Calculates the totalAmount by applying discount and tax.
     *
     * Formula:
     *     Subtotal = price × quantity
     *     Discounted = Subtotal - discountAmt
     *     Tax = Discounted × taxRate
     *     totalAmount = Discounted + Tax
     *
     * This method should be called after setting all price-related fields.
     */
    public void computeTotal() {
        // Subtotal = price * quantity
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));

        // Apply discount
        BigDecimal discounted = subtotal.subtract(discountAmt);

        // Add tax
        this.totalAmount = discounted.add(discounted.multiply(taxRate));
    }
}