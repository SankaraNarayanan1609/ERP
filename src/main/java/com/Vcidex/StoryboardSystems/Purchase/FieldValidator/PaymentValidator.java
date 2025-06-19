package com.Vcidex.StoryboardSystems.Purchase.FieldValidator;

import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.aventstack.extentreports.ExtentTest;

public class PaymentValidator {

    public static void compareWithInvoice(PaymentData paymentData, ReceiveInvoicePage invoicePage, ExtentTest node) {
        // Validate Invoice Reference No
        ValidationLogger.assertEquals("Invoice Ref", paymentData.getInvoiceRefNo(), invoicePage.getInvoiceRefNo(), node);

        // Compare Payment Amount vs Grand Total (strings directly)
        ValidationLogger.assertEquals("Payment Amount", paymentData.getPaymentAmount().toPlainString(), invoicePage.getGrandTotal(), node);

        // Check that Payment Date is same or after Invoice Date
        boolean isDateValid = !paymentData.getPaymentDate().isBefore(invoicePage.getInvoiceDate());
        ValidationLogger.assertTrue("Payment date is same or after Invoice date", isDateValid, node);

        // Confirm Payment Mode is non-empty
        ValidationLogger.assertTrue("Payment mode is not empty", paymentData.getPaymentMode() != null && !paymentData.getPaymentMode().isEmpty(), node);
    }
}