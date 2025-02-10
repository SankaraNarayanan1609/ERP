package com.Vcidex.StoryboardSystems.Common.Workflow.Approval;

import com.Vcidex.StoryboardSystems.Common.Workflow.DynamicApprovalHandler;

public class GenericApproval extends DynamicApprovalHandler {
    private final String approvalLevel;

    public GenericApproval(String approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    @Override
    protected boolean approve(String clientID, String requestType) {
        System.out.println("✅ [" + approvalLevel + "] Approved for Client: " + clientID + " | Request: " + requestType);
        return true; // Simulating approval process
    }
}
