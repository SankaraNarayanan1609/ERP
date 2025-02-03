package com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow;

abstract class ApprovalHandler {
    protected ApprovalHandler nextHandler;

    public void setNextHandler(ApprovalHandler handler) {
        this.nextHandler = handler;
    }

    public void processRequest(String approvalType) {
        if (nextHandler != null) {
            nextHandler.processRequest(approvalType);
        }
    }
}

class POApproval extends ApprovalHandler {
    @Override
    public void processRequest(String approvalType) {
        if ("PO".equals(approvalType)) {
            System.out.println("✅ PO Approved");
        } else {
            super.processRequest(approvalType);
        }
    }
}

class InvoiceApproval extends ApprovalHandler {
    @Override
    public void processRequest(String approvalType) {
        if ("INVOICE".equals(approvalType)) {
            System.out.println("✅ Invoice Approved");
        } else {
            super.processRequest(approvalType);
        }
    }
}

