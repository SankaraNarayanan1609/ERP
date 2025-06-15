package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class PurchaseInvoiceData {

    private String           branchName;
    private String           invoiceRefNo;
    private LocalDate        invoiceDate;
    private LocalDate        dueDate;
    private String           vendorName;
    private String           vendorDetails;
    private String           billTo;
    private String           shipTo;
    private String           requestedBy;
    private String           requestorContactDetails;
    private String           deliveryTerms;
    private String           paymentTerms;
    private String           purchaseType;
    private String           dispatchMode;
    private String           currency;
    private BigDecimal       exchangeRate;
    private String           coverNote;
    private boolean          renewal;
    private LocalDate        renewalDate;
    private String           frequency;
    private String           termsAndConditions;
    private String           termsEditorText;
    private List<LineItem>   lineItems;
    private BigDecimal       netAmount;
    private BigDecimal       addOnCharges;
    private BigDecimal       additionalDiscount;
    private BigDecimal       freightCharges;
    private String           additionalTax;
    private BigDecimal       roundOff;
    private BigDecimal       grandTotal;
    private String           remarks;
    private String           billingEmail;
    private String           termsTemplate;
    private String           termsContent;

    public void computeNetAmount() {
        this.netAmount = lineItems.stream()
                .map(LineItem::getTotalAmount)
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
                // assume zero
            }
        }
        BigDecimal taxAmt   = interim.multiply(pct).setScale(2, RoundingMode.HALF_UP);
        BigDecimal rawTotal = interim.add(taxAmt);
        BigDecimal rounded  = rawTotal.setScale(0, RoundingMode.HALF_UP);
        this.roundOff   = rounded.subtract(rawTotal);
        this.grandTotal = rawTotal.add(roundOff);
    }
}