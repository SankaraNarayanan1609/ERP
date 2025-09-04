package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LineItem {

    private String productGroup;
    private String productCode;
    private String productName;
    private String description;
    private double quantity;

    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal discountPct = BigDecimal.ZERO;

    private BigDecimal discountAmt;
    private String taxPrefix;
    private BigDecimal taxRate;
    private BigDecimal totalAmount;
    private Product product;

    @Builder.Default
    private boolean success = false;
    private String failureReason;
    private int index;

    public BigDecimal computeTotalAmount() {
        BigDecimal qty  = BigDecimal.valueOf(this.quantity);
        BigDecimal p    = this.price != null ? this.price : BigDecimal.ZERO;
        BigDecimal base = p.multiply(qty);

        BigDecimal dPct = this.discountPct != null ? this.discountPct : BigDecimal.ZERO;
        BigDecimal discount = base.multiply(dPct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return base.subtract(discount);
    }

    public BigDecimal computeAndGetTotal() {
        BigDecimal qty  = BigDecimal.valueOf(this.quantity);
        BigDecimal p    = this.price != null ? this.price : BigDecimal.ZERO;

        // base = price * qty
        BigDecimal base = p.multiply(qty);

        // discount = max(discountAmt, base * discountPct/100)
        BigDecimal pct  = this.discountPct != null ? this.discountPct : BigDecimal.ZERO;
        BigDecimal pctAmt = base.multiply(pct).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal dAmt = this.discountAmt != null ? this.discountAmt : BigDecimal.ZERO;
        BigDecimal discount = dAmt.max(pctAmt); // choose your rule; or add them if that’s your business logic

        BigDecimal taxable = base.subtract(discount).max(BigDecimal.ZERO);

        BigDecimal tRate = this.taxRate != null ? this.taxRate : BigDecimal.ZERO; // e.g., 0.18
        BigDecimal tAmt  = taxable.multiply(tRate).setScale(2, RoundingMode.HALF_UP);

        this.totalAmount = taxable.add(tAmt).setScale(2, RoundingMode.HALF_UP);
        return this.totalAmount;
    }

    public void computeTotal() {
        computeAndGetTotal();
    }
}