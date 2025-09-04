// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Support/PurchaseOrderMemory.java
package com.Vcidex.StoryboardSystems.Purchase.Support;

public final class PurchaseOrderMemory {
    private static final ThreadLocal<String> PO_REF = new ThreadLocal<>();

    private PurchaseOrderMemory() {}

    public static void set(String poRef) { PO_REF.set(poRef); }

    public static String get() { return PO_REF.get(); }

    public static void clear() { PO_REF.remove(); }
}