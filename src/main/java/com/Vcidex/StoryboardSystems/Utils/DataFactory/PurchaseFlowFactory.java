package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.Inventory.MaterialInwardDataFactory;
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderLine;

import java.util.List;

public class PurchaseFlowFactory {

    private final PurchaseOrderDataFactory poFactory;
    private final PurchaseInvoiceDataFactory invoiceFactory;
    // private final PaymentDataFactory paymentFactory;

    public PurchaseFlowFactory(ApiMasterDataProvider provider) {
        this.poFactory      = new PurchaseOrderDataFactory(provider);
        this.invoiceFactory = new PurchaseInvoiceDataFactory(provider);
        // No need to initialize MaterialInwardDataFactory — it is static-only
    }

    public PurchaseOrderData createDirectPO(boolean isRenewal) {
        return poFactory.create(isRenewal);
    }

    public MaterialInwardData createInwardFromPO(PurchaseOrderData po) {
        MaterialInwardData inward = MaterialInwardDataFactory.createFromPO(po);

        List<PurchaseOrderLine> poLines = po.getLineItems().stream()
                .map(li -> new PurchaseOrderLine(li.getProductName(), li.getQuantity()))
                .toList();

        for (int i = 0; i < poLines.size(); i++) {
            int orderedQty = poLines.get(i).getOrderedQty();
            inward.getReceivedQtyByRow().put(i + 1, String.valueOf(orderedQty));
        }

        return inward;
    }

    public PurchaseInvoiceData createInvoiceFromPO(PurchaseOrderData po) {
        return invoiceFactory.createFromPO(po);
    }

    // public PaymentData createPaymentFromInvoice(PurchaseInvoiceData invoice) {
    //     return paymentFactory.createFromInvoice(invoice);
    // }
}