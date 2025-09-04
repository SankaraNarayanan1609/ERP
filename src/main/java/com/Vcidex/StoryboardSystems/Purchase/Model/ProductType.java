package com.Vcidex.StoryboardSystems.Purchase.Model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ProductType {
    PHYSICAL,
    SERVICE;

    private static boolean looksService(String s) {
        if (s == null) return false;
        String x = s.trim().toLowerCase();
        if (x.isEmpty()) return false;
        // "service", "services", "subscription", "non stock"/"non-stock"
        return x.startsWith("serv") || x.contains("subscription") || x.contains("non stock") || x.contains("non-stock");
    }

    /**
     * Liberal parser:
     * - If it looks like "service" (or similar) → SERVICE
     * - Otherwise → PHYSICAL
     * Returns null only when raw is null/blank (so callers can decide a default).
     */
    public static ProductType fromApiString(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        return looksService(s) ? SERVICE : PHYSICAL;
    }

    /** Prefer names from master; else fall back to raw product_type. */
    public static ProductType fromNames(String productTypeName, String productTypeNameAlt, String productTypeRaw) {
        if (looksService(productTypeName) || looksService(productTypeNameAlt)) return SERVICE;
        ProductType t = fromApiString(productTypeRaw);
        return t == null ? PHYSICAL : t; // safe default
    }

    @JsonCreator
    static ProductType fromJson(Object value) {
        return fromApiString(value == null ? null : value.toString());
    }
}