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

        data.setInvoiceRefNo(invoice.getInvoiceRefNo());
        data.setVendorName(invoice.getVendorName()); // <--- needed by navigator

        // Payment Date ≥ Invoice Date
        LocalDate paymentDate = invoice.getInvoiceDate()
                .plusDays(faker.number().numberBetween(0, 3));
        data.setPaymentDate(paymentDate);

        data.setPaymentRemarks("Auto-payment for invoice " + invoice.getInvoiceRefNo());
        data.setPaymentNote("Paid via automation on " + paymentDate);
        data.setPaymentMode("Cash");

        // Ensure totals are present
        if (invoice.getGrandTotal() == null || invoice.getGrandTotal().signum() == 0) {
            invoice.computeNetAmount();
            invoice.computeGrandTotal();
        }
        data.setPaymentAmount(invoice.getGrandTotal());  // requires grandTotal field
        return data;
    }
}