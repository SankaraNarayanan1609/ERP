package com.Vcidex.StoryboardSystems.Purchase.Model;

public enum ProductType {

    PHYSICAL,
    ASSET,
    FINISHED_GOODS,
    RAW_MATERIALS,
    CONSUMABLE,
    TRADABLE_GOODS,
    SERVICE;           // singular in enum

    public static ProductType fromApiString(String raw) {
        if (raw == null) return null;
        switch(raw.trim().toLowerCase()) {
            case "asset":              return ASSET;
            case "finished goods":     return FINISHED_GOODS;
            case "raw materials":      return RAW_MATERIALS;
            case "consumable":         return CONSUMABLE;
            case "tradable goods":     return TRADABLE_GOODS;
            case "services":           // ← JSON uses plural
            case "service":
                return SERVICE;
            default:
                throw new IllegalArgumentException("Unknown product type: " + raw);
        }
    }
}
