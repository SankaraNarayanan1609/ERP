// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/POJO/LineItem.java
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

    public BigDecimal getDiscount() {
        return discountPct;
    }

    /** helper to calculate totalAmount **/
    public void computeTotal() {
        BigDecimal sub = price
                .multiply(BigDecimal.valueOf(quantity))
                .subtract(discountAmt);
        this.totalAmount = sub.add(sub.multiply(taxRate));
    }
}