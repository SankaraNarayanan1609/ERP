package com.Vcidex.StoryboardSystems.Common.Workflow;

import com.Vcidex.StoryboardSystems.Utils.Database.DatabaseService;
public class WorkflowFactory {
    public static WorkflowOrchestrator createWorkflow(String clientID) {
        String poId = DatabaseService.fetchPoIdByClient(clientID);

        if (poId == null) {
            throw new RuntimeException("❌ No PO ID found for Client [" + clientID + "]");
        }

        return new WorkflowOrchestrator(clientID, poId);
    }
}