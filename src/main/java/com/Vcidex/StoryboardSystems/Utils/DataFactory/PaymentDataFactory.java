package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.Random;

public class PaymentDataFactory {

    private final Faker faker = new Faker(new Random(System.currentTimeMillis()));

    /**
     * Generate PaymentData based on the Invoice context.
     *
     * Rules:
     * - Payment Date >= Invoice Date
     * - Payment Mode = "Cash" only for now
     * - Payment Amount = Outstanding amount from Invoice (rounded to 2 decimals)
     */
    public PaymentData createFromInvoice(PurchaseInvoiceData invoice) {
        PaymentData data = new PaymentData();

        // 1) Reference Invoice
        data.setInvoiceRefNo(invoice.getInvoiceRefNo());

        // 2) Payment Date >= Invoice Date
        LocalDate invoiceDate = invoice.getInvoiceDate();
        LocalDate paymentDate = invoiceDate.plusDays(faker.number().numberBetween(0, 3));
        data.setPaymentDate(paymentDate);

        // 3) Remarks and Notes
        data.setPaymentRemarks("Auto-payment for invoice " + invoice.getInvoiceRefNo());
        data.setPaymentNote("Paid via automation on " + paymentDate);

        // 4) Payment Mode: currently fixed as "Cash"
        data.setPaymentMode("Cash");

        // 5) Payment Amount = Grand Total from invoice
        data.setPaymentAmount(invoice.getGrandTotal());

        return data;
    }
}
