package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Builder
public class LineItem {
    private String     productGroup;
    private String     productCode;
    private String     productName;
    private String     description;
    private int        quantity;
    private BigDecimal price;
    private BigDecimal discountPct;
    private BigDecimal discountAmt;
    private String     taxPrefix;
    private BigDecimal taxRate;
    private BigDecimal totalAmount;

    // Custom calculation
    public void computeTotal() {
        if (price == null) throw new IllegalStateException("LineItem price is null for " + productCode);
        if (discountAmt == null) throw new IllegalStateException("LineItem discountAmt is null for " + productCode);
        if (taxRate == null) throw new IllegalStateException("LineItem taxRate is null for " + productCode);
        if (quantity <= 0) throw new IllegalStateException("LineItem quantity is zero or negative for " + productCode);

        BigDecimal sub = price.multiply(BigDecimal.valueOf(quantity)).subtract(discountAmt);
        this.totalAmount = sub.add(sub.multiply(taxRate));
    }
}