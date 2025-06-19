package com.Vcidex.StoryboardSystems.Purchase.FieldValidator;

import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Utils.Logger.*;
import org.openqa.selenium.WebDriver;

import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderLine;
import com.aventstack.extentreports.ExtentTest;

import java.util.List;

public class ReceiveInvoiceValidator {

    /**
     * Compares field-by-field data from PO to the Invoice screen.
     *
     * @param poData    The original PO data object
     * @param invoicePage The ReceiveInvoicePage object (after fill)
     * @param driver    WebDriver for screenshot/log capture on failure
     */
    public static void verifyInvoiceAgainstPO(PurchaseOrderData poData, ReceiveInvoicePage invoicePage, WebDriver driver) {
        ValidationLogger.reset();

        // Branch Name
        ValidationLogger.assertEquals("Branch Name", poData.getBranchName(), invoicePage.getBranchName(), driver);

        // Vendor Name
        ValidationLogger.assertEquals("Vendor Name", poData.getVendorName(), invoicePage.getVendorName(), driver);

        // Bill To / Vendor Details
        ValidationLogger.assertEquals("Vendor Details (Bill To)", poData.getBillTo(), invoicePage.getBillTo(), driver);

        // Ship To
        ValidationLogger.assertEquals("Ship To", poData.getShipTo(), invoicePage.getShipTo(), driver);

        // Delivery Terms
        ValidationLogger.assertEquals("Delivery Terms", poData.getDeliveryTerms(), invoicePage.getDeliveryTerms(), driver);

        // Payment Terms
        ValidationLogger.assertEquals("Payment Terms", poData.getPaymentTerms(), invoicePage.getPaymentTerms(), driver);

        // Dispatch Mode
        ValidationLogger.assertEquals("Dispatch Mode", poData.getDispatchMode(), invoicePage.getDispatchMode(), driver);

        // Currency
        ValidationLogger.assertEquals("Currency", poData.getCurrency(), invoicePage.getCurrency(), driver);

        // Exchange Rate
        ValidationLogger.assertEquals("Exchange Rate", poData.getExchangeRate().toPlainString(), invoicePage.getExchangeRate(), driver);

        // Terms & Conditions Text
        ValidationLogger.assertEquals("Terms and Conditions (Editor)", poData.getTermsEditorText(), invoicePage.getTermsEditorText(), driver);

        // Net Amount
        ValidationLogger.assertEquals("Net Amount", poData.getNetAmount().toPlainString(), invoicePage.getNetAmount(), driver);

        // Grand Total
        ValidationLogger.assertEquals("Grand Total", poData.getGrandTotal().toPlainString(), invoicePage.getGrandTotal(), driver);

        // TODO: Loop through Line Items
        // (Optional for now—if needed, we can compare each LineItem in a separate method.)

        ValidationLogger.assertAll("ReceiveInvoice vs DirectPO");
    }

    /**
     * Compares line items between Purchase Order and Invoice for consistency.
     *
     * @param poData       PurchaseOrderData object from original PO
     * @param invoicePage  Page Object for the Invoice UI
     * @param node         ExtentTest node for reporting
     */
    public static void compareLineItems(PurchaseOrderData poData, ReceiveInvoicePage invoicePage, ExtentTest node) {
        ReportManager.setTest(node);

        List<LineItem> poItems = poData.getLineItems();                       // From PO data
        List<PurchaseOrderLine> invoiceItems = invoicePage.getLineItems();   // From Invoice UI

        ValidationLogger.assertEquals("Line item count",
                String.valueOf(poItems.size()),
                String.valueOf(invoiceItems.size()),
                node
        );

        for (int i = 0; i < poItems.size(); i++) {
            LineItem poLine = poItems.get(i);
            PurchaseOrderLine invLine = invoiceItems.get(i);

            ValidationLogger.assertEquals(
                    "Product Name (Row " + (i + 1) + ")",
                    poLine.getProductName(),
                    invLine.getProductName(),
                    node
            );

            ValidationLogger.assertEquals(
                    "Product Quantity (Row " + (i + 1) + ")",
                    String.valueOf(poLine.getQuantity()),
                    String.valueOf(invLine.getOrderedQty()),
                    node
            );
        }
    }
}