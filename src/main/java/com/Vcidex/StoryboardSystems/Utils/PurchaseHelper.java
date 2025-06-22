package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;
import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;

public class PurchaseHelper {

    /**
     * Count all line-items whose product type is NOT SERVICE.
     */
    public static int getNonServiceItemCount(PurchaseOrderData data) {
        return (int) data.getLineItems().stream()
                .filter(line -> {
                    Product p = line.getProduct();
                    return p != null && p.getProductType() == ProductType.SERVICE;
                })
                .count();
    }
}