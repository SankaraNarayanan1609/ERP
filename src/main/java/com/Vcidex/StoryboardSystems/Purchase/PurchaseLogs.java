package com.Vcidex.StoryboardSystems.Purchase;

public class PurchaseLogs {
    public static class PO {
        public static String created(String poNumber, String vendor) {
            return String.format("📦 PO Created → PO Number: %s | Vendor: %s", poNumber, vendor);
        }

        public static String approved(String poNumber, String approver) {
            return String.format("✅ PO Approved → PO: %s | By: %s", poNumber, approver);
        }

        public static String cancelled(String poNumber, String reason) {
            return String.format("🚫 PO Cancelled → PO: %s | Reason: %s", poNumber, reason);
        }

        public static String error(String poNumber, String errorContext) {
            return String.format("❗ Error with PO %s | Context: %s", poNumber, errorContext);
        }
    }
}
