package com.Vcidex.StoryboardSystems.LogMessages;

public class PurchaseLogs {

    public static class PO {
        public static String created(String poNumber, String vendor) {
            return String.format("📦 PO Created → PO Number: %s | Vendor: %s", poNumber, vendor);
        }

        public static String approved(String poNumber, String approver) {
            return String.format("✅ PO Operational Approval Approved → PO: %s | By: %s", poNumber, approver);
        }

        public static String cancelled(String poNumber, String reason) {
            return String.format("🚫 PO Cancelled → PO: %s | Reason: %s", poNumber, reason);
        }

        public static String error(String poNumber, String errorContext) {
            return String.format("❗ Error with PO %s | Context: %s", poNumber, errorContext);
        }
    }

    public static class Invoice {
        public static String created(String invNumber) {
            return String.format("🧾 Direct Invoice Created → Invoice Number: %s", invNumber);
        }

        public static String error(String step, Throwable t) {
            return String.format("❗ Error during Invoice step: %s | Exception: %s", step, t.getMessage());
        }
    }

    public static class Inward {
        public static String started() {
            return "📥 Started Material Inward creation.";
        }

        public static String details(String deliveryNo) {
            return String.format("📄 Entered Inward basic details (Delivery No: %s).", deliveryNo);
        }

        public static String addedProducts(int count) {
            return String.format("📦 Added %d product(s) to Material Inward.", count);
        }

        public static String submitted() {
            return "✅ Submitted Material Inward form.";
        }

        public static String summaryValidated(String inwardNo) {
            return String.format("📋 Validated Inward Number '%s' in Summary screen.", inwardNo);
        }

        public static String completed() {
            return "🎉 Completed Material Inward creation successfully.";
        }
    }

    public static class QC {
        public static String started() {
            return "▶️ Started Inward QC for received goods.";
        }

        public static String details(String deliveryNo, String vendor) {
            return String.format("📝 Entered QC details - Delivery No: '%s', Vendor: '%s'", deliveryNo, vendor);
        }

        public static String product(String group, String name) {
            return String.format("🔍 QC performed on Product Group: '%s', Product: '%s'", group, name);
        }

        public static String quantity(String inwardQty, String deliveredQty) {
            return String.format("📦 Quantity Verification - Inward Qty: '%s', Delivered Qty: '%s'", inwardQty, deliveredQty);
        }

        public static String rejected(String rejectedQty) {
            return String.format("❌ Rejected Quantity Logged: '%s'", rejectedQty);
        }

        public static String shortage(String shortageQty) {
            return String.format("⚠️ Shortage Quantity Logged: '%s'", shortageQty);
        }

        public static String remarks(String remarks) {
            return String.format("💬 QC Remarks added: '%s'", remarks);
        }

        public static String submitted() {
            return "✅ Inward QC form submitted.";
        }

        public static String completed(String deliveryNo) {
            return String.format("🔒 QC process completed and validated in summary for Delivery No: '%s'", deliveryNo);
        }
    }
}