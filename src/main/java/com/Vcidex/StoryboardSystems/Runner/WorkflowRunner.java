package com.Vcidex.StoryboardSystems.Runner;

import com.Vcidex.StoryboardSystems.Common.Workflow.FeatureToggleService;
import com.Vcidex.StoryboardSystems.Common.Workflow.Approval.ApprovalProcessor;
import com.Vcidex.StoryboardSystems.Utils.Database.DatabaseService;

import java.util.List;
import java.util.Map;

public class WorkflowRunner {
    public static void main(String[] args) {
        // ✅ Load approval rules dynamically from DB/API
        FeatureToggleService.loadApprovalChainsFromDB();

        // ✅ Fetch all clients dynamically from the database
        Map<String, List<String>> clientWorkflows = DatabaseService.getPendingApprovalClients();

        // ✅ Iterate over all clients & process their workflows dynamically
        for (Map.Entry<String, List<String>> entry : clientWorkflows.entrySet()) {
            String clientID = entry.getKey();
            List<String> requestTypes = entry.getValue();

            for (String requestType : requestTypes) {
                ApprovalProcessor.startApprovalProcess(clientID, requestType);
            }
        }
    }
}