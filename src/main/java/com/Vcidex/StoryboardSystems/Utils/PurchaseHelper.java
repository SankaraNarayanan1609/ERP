package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;

public class PurchaseHelper {

    public static int getNonServiceItemCount(PurchaseOrderData data) {
        return (int) data.getLineItems().stream()
                .filter(line -> {
                    Product product = line.getProduct();
                    return product == null || product.getProductType() == null ||
                            !product.getProductType().equalsIgnoreCase("service");
                })
                .count();
    }
}
