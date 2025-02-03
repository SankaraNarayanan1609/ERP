package com.Vcidex.StoryboardSystems.Common.Workflow;

public class WorkflowFactory {
    public static WorkflowOrchestrator createWorkflow() {
        return new WorkflowOrchestrator();
    }
}