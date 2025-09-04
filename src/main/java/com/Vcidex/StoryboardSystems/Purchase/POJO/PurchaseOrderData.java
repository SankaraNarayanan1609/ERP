package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.math.BigDecimal;       // For monetary values
import java.math.RoundingMode;     // For rounding logic in tax/grand total
import java.time.LocalDate;        // Represents dates like PO date, expected date
import java.util.List;             // Used for holding multiple LineItems

import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import lombok.Builder;             // Lombok: Auto-generates builder pattern for this POJO
import lombok.Data;                // Lombok: Auto-generates getters, setters, toString, equals, hashCode

@Data
@Builder(toBuilder = true)
public class PurchaseOrderData {

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

    private boolean renewal;
    private LocalDate renewalDate;
    private String frequency;

    private String termsAndConditions;
    private String termsEditorText;

    private List<LineItem> lineItems;

    private BigDecimal netAmount;
    private BigDecimal addOnCharges;
    private BigDecimal additionalDiscount;
    private BigDecimal freightCharges;
    private String additionalTax;

    private BigDecimal roundOff;
    private BigDecimal grandTotal;

    private ProductType productType;
    private String currencyCode;

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public List<LineItem> getLineItems() {
        return this.lineItems;
    }

    public void computeNetAmount() {
        this.netAmount = lineItems.stream()
                .map(LineItem::computeTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void computeGrandTotal() {
        if (netAmount == null) netAmount = BigDecimal.ZERO;

        BigDecimal interim = netAmount
                .add(addOnCharges != null ? addOnCharges : BigDecimal.ZERO)
                .subtract(additionalDiscount != null ? additionalDiscount : BigDecimal.ZERO)
                .add(freightCharges != null ? freightCharges : BigDecimal.ZERO);

        BigDecimal pct = BigDecimal.ZERO;
        if (additionalTax != null && additionalTax.contains("%")) {
            try {
                String num = additionalTax.replace("%", "").trim();
                pct = new BigDecimal(num)
                        .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
            } catch (NumberFormatException e) {
                // fallback
            }
        }

        BigDecimal taxAmt   = interim.multiply(pct).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rawTotal = interim.add(taxAmt);

        BigDecimal rounded  = rawTotal.setScale(0, RoundingMode.HALF_UP);
        this.roundOff       = rounded.subtract(rawTotal);
        this.grandTotal     = rawTotal.add(roundOff);
    }
}