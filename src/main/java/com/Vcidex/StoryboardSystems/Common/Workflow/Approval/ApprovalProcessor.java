package com.Vcidex.StoryboardSystems.Common.Workflow.Approval;

import com.Vcidex.StoryboardSystems.Common.Workflow.DynamicApprovalHandler;
import com.Vcidex.StoryboardSystems.Common.Workflow.FeatureToggleService;

import java.util.List;

public class ApprovalProcessor {
    public static void startApprovalProcess(String clientID, String requestType) {
        List<String> approvalHierarchy = FeatureToggleService.getApprovalChain(clientID, requestType);

        if (approvalHierarchy.isEmpty()) {
            System.out.println("⚡ No approval required for: " + requestType + " (Client: " + clientID + ")");
            return;
        }

        DynamicApprovalHandler firstApprover = buildApprovalChain(approvalHierarchy);
        firstApprover.processApproval(clientID, requestType);
    }

    private static DynamicApprovalHandler buildApprovalChain(List<String> approvalHierarchy) {
        DynamicApprovalHandler firstHandler = null;
        DynamicApprovalHandler previousHandler = null;

        for (String approvalLevel : approvalHierarchy) {
            DynamicApprovalHandler currentHandler = new GenericApproval(approvalLevel);

            if (firstHandler == null) {
                firstHandler = currentHandler;
            }

            if (previousHandler != null) {
                previousHandler.setNextApprover(currentHandler);
            }

            previousHandler = currentHandler;
        }
        return firstHandler;
    }
}
