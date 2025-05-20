// File: src/main/java/com/Vcidex/StoryboardSystems/Purchase/POJO/LineItem.java
package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.math.BigDecimal;

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

    // Getters & Setters
    public String getProductGroup() {
        return productGroup;
    }
    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPct() {
        return discountPct;
    }
    public void setDiscountPct(BigDecimal discountPct) {
        this.discountPct = discountPct;
    }
    public BigDecimal getDiscountAmt() {
        return discountAmt;
    }
    public void setDiscountAmt(BigDecimal discountAmt) {
        this.discountAmt = discountAmt;
    }
    public String getTaxPrefix() {
        return taxPrefix;
    }
    public void setTaxPrefix(String taxPrefix) {
        this.taxPrefix = taxPrefix;
    }
    public BigDecimal getTaxRate() {
        return taxRate;
    }
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }
        public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Calculation helper
    public void computeTotal() {
        BigDecimal sub = price.multiply(BigDecimal.valueOf(quantity))
                .subtract(discountAmt);
        this.totalAmount = sub.add(sub.multiply(taxRate));
    }
}