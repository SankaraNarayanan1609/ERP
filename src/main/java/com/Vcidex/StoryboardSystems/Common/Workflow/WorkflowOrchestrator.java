package com.Vcidex.StoryboardSystems.Common.Workflow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class WorkflowOrchestrator {
    private static final Logger logger = LogManager.getLogger(WorkflowOrchestrator.class);
    private static final String FILE_PATH = "src/test/resources/workflow-state.json";

    private WorkflowState currentState;
    private final String clientID;
    private final String poId;

    public WorkflowOrchestrator(String clientID, String poId) {
        this.clientID = clientID;
        this.poId = poId;
        this.currentState = loadWorkflowState(); // ✅ Load from JSON file
    }

    public void nextStep() {
        String productType = RuleEngine.getProductType(poId);
        String workflowStage = RuleEngine.getWorkflowStageForProductType(productType);

        logger.info("🔄 Current PO: {} | Product Type: {} | Workflow Stage: {}", poId, productType, workflowStage);

        switch (currentState) {
            case PURCHASE_INDENT:
                currentState = WorkflowState.DIRECT_PO;
                break;
            case DIRECT_PO:
                currentState = workflowStage.equals("INVOICE") ? WorkflowState.INVOICE : WorkflowState.INWARD;
                break;
            case INWARD:
                currentState = WorkflowState.INVOICE;
                break;
            case INVOICE:
                if (FeatureToggleService.isApprovalRequired(clientID, "INVOICE_APPROVAL")) {
                    List<String> approvalLevels = FeatureToggleService.getApprovalChain(clientID, "INVOICE_APPROVAL");
                    logger.info("🔍 Invoice Approval Needed: Approval Levels -> {}", approvalLevels);
                    currentState = WorkflowState.INVOICE_APPROVAL;
                } else {
                    currentState = WorkflowState.PAYMENT;
                }
                break;
            case INVOICE_APPROVAL:
                currentState = WorkflowState.PAYMENT;
                break;
            case PAYMENT:
                currentState = WorkflowState.COMPLETED;
                logger.info("✅ Workflow Completed!");
                break;
            default:
                logger.info("✅ Workflow Completed!");
                break;
        }

        saveWorkflowState(); // ✅ Save state after each transition
        logger.info("🔄 New Workflow State: {}", currentState);
    }

    private void saveWorkflowState() {
        try {
            File file = new File(FILE_PATH);
            JSONObject workflowData = file.exists()
                    ? new JSONObject(new String(Files.readAllBytes(file.toPath())))
                    : new JSONObject();

            workflowData.put(poId, new JSONObject()
                    .put("clientID", clientID)
                    .put("state", currentState.name()));

            try (FileWriter writer = new FileWriter(FILE_PATH)) {
                writer.write(workflowData.toString(4)); // Pretty print
            }
        } catch (Exception e) {
            logger.error("❌ Error saving workflow state: {}", e.getMessage());
        }
    }

    private WorkflowState loadWorkflowState() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                JSONObject workflowData = new JSONObject(new String(Files.readAllBytes(file.toPath())));
                if (workflowData.has(poId)) {
                    return WorkflowState.valueOf(workflowData.getJSONObject(poId).getString("state"));
                }
            }
        } catch (Exception e) {
            logger.warn("⚠️ No previous workflow state found for PO [{}], starting from PURCHASE_INDENT", poId);
        }
        return WorkflowState.PURCHASE_INDENT;
    }

    public WorkflowState getCurrentState() {
        return currentState;
    }
}