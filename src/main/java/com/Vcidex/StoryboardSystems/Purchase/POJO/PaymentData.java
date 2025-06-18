/**
 * POJO class that holds data for creating a payment transaction.
 * Used in both test data (factories) and UI forms (Page Object).
 *
 * It includes references to invoice, payment details, and amounts.
 */

package com.Vcidex.StoryboardSystems.Purchase.POJO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PaymentData {

    private String invoiceRefNo;      // Invoice against which payment is made
    private LocalDate paymentDate;    // When payment is recorded
    private String paymentRemarks;    // Short text for context
    private String paymentNote;       // Additional note/instruction
    private String paymentMode;       // Mode of payment (e.g., Cash, UPI, NEFT)
    private BigDecimal paymentAmount; // Actual amount paid

    /**
     * Full constructor used in tests or backend models.
     */
    public PaymentData(String invoiceRefNo, LocalDate paymentDate, String paymentRemarks,
                       String paymentNote, String paymentMode, BigDecimal paymentAmount) {
        this.invoiceRefNo = invoiceRefNo;
        this.paymentDate = paymentDate;
        this.paymentRemarks = paymentRemarks;
        this.paymentNote = paymentNote;
        this.paymentMode = paymentMode;
        this.paymentAmount = paymentAmount;
    }

    /**
     * Default constructor for deserialization or manual field population.
     */
    public PaymentData() {}
}