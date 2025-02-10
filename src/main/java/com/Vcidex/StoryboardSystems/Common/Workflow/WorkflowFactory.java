package com.Vcidex.StoryboardSystems.Common.Workflow;

public class WorkflowFactory {
    public static WorkflowOrchestrator createWorkflow(String clientID) {
        return new WorkflowOrchestrator(clientID);
    }
}