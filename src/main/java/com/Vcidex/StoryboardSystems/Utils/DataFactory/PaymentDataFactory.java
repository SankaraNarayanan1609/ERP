/**
 * Factory class to create test data for payment flows.
 * It generates a valid PaymentData object using the associated invoice.
 *
 * Used in automation frameworks to simulate payment creation dynamically.
 */

package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.Random;

public class PaymentDataFactory {

    private final Faker faker = new Faker(new Random(System.currentTimeMillis()));

    /**
     * Create a valid PaymentData object from a given Invoice.
     *
     * Business Rules:
     * - Payment Date ≥ Invoice Date
     * - Mode is always "Cash" (can be extended)
     * - Amount must match invoice grand total
     *
     * @param invoice Invoice object as input
     * @return populated PaymentData instance
     */
    public PaymentData createFromInvoice(PurchaseInvoiceData invoice) {
        PaymentData data = new PaymentData();

        // Step 1: Link to invoice reference number
        data.setInvoiceRefNo(invoice.getInvoiceRefNo());

        // Step 2: Set payment date ≥ invoice date
        LocalDate invoiceDate = invoice.getInvoiceDate();
        LocalDate paymentDate = invoiceDate.plusDays(faker.number().numberBetween(0, 3));
        data.setPaymentDate(paymentDate);

        // Step 3: Add remarks and notes
        data.setPaymentRemarks("Auto-payment for invoice " + invoice.getInvoiceRefNo());
        data.setPaymentNote("Paid via automation on " + paymentDate);

        // Step 4: Hardcoded payment mode for now
        data.setPaymentMode("Cash");

        // Step 5: Match payment amount to invoice total
        data.setPaymentAmount(invoice.getGrandTotal());

        return data;
    }
}