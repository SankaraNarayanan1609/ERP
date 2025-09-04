// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Support/PurchaseIndentMemory.java
package com.Vcidex.StoryboardSystems.Purchase.Support;

public final class PurchaseIndentMemory {
    private static final ThreadLocal<String> BRANCH = new ThreadLocal<>();
    private static final ThreadLocal<String> INDENT = new ThreadLocal<>();

    private PurchaseIndentMemory(){}

    public static void set(String branch, String indentNo) {
        BRANCH.set(branch);
        INDENT.set(indentNo);
    }
    public static String branch(){ return BRANCH.get(); }
    public static String indent(){ return INDENT.get(); }
    public static void clear(){ BRANCH.remove(); INDENT.remove(); }
}