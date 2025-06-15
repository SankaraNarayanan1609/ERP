package com.Vcidex.StoryboardSystems.Purchase.POJO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentData {
    private String invoiceRefNo;          // Reference to the Invoice
    private LocalDate paymentDate;        // Date of Payment
    private String paymentRemarks;        // Remarks for the payment
    private String paymentNote;           // Additional note
    private String paymentMode;           // Payment mode (Cash only for now)
    private BigDecimal paymentAmount;     // Amount to be paid (matches outstanding)

    public PaymentData(String invoiceRefNo, LocalDate paymentDate, String paymentRemarks,
                       String paymentNote, String paymentMode, BigDecimal paymentAmount) {
        this.invoiceRefNo = invoiceRefNo;
        this.paymentDate = paymentDate;
        this.paymentRemarks = paymentRemarks;
        this.paymentNote = paymentNote;
        this.paymentMode = paymentMode;
        this.paymentAmount = paymentAmount;
    }

    public PaymentData() {
        // Default constructor
    }
}
