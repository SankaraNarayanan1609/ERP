package com.Vcidex.StoryboardSystems.Common.Workflow;

public abstract class DynamicApprovalHandler {
    protected DynamicApprovalHandler nextApprover;

    public void setNextApprover(DynamicApprovalHandler nextApprover) {
        this.nextApprover = nextApprover;
    }

    public void processApproval(String clientID, String requestType) {
        if (approve(clientID, requestType)) {
            if (nextApprover != null) {
                nextApprover.processApproval(clientID, requestType);
            } else {
                System.out.println("✅ Final Approval Completed for Client: " + clientID + " | Request: " + requestType);
            }
        } else {
            System.out.println("❌ Approval Rejected at: " + this.getClass().getSimpleName());
        }
    }

    protected abstract boolean approve(String clientID, String requestType);
}
