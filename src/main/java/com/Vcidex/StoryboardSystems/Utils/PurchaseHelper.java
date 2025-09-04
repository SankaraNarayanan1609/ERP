package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;

import java.util.Objects;

public class PurchaseHelper {

    /**
     * Count all line-items whose product type is NOT SERVICE.
     */
    public static int getNonServiceItemCount(PurchaseOrderData data) {
        return (int) data.getLineItems().stream()
                .map(LineItem::getProduct)
                .filter(Objects::nonNull)
                .filter(p -> p.getProductType() != ProductType.SERVICE)   // ← fixed
                .count();
    }
}