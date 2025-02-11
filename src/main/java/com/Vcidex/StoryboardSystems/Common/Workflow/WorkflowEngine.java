package com.Vcidex.StoryboardSystems.Common.Workflow;

import com.Vcidex.StoryboardSystems.Inventory.InventoryWorkflow.InventoryWorkflowEngine;
import com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow.PurchaseWorkflowEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.List;
import com.Vcidex.StoryboardSystems.Utils.Database.DatabaseService;

public class WorkflowEngine {
    private static final Logger logger = LogManager.getLogger(WorkflowEngine.class);
    private final String clientID;
    private final PurchaseWorkflowEngine purchaseWorkflowEngine;

    public WorkflowEngine(String clientID) {
        this.clientID = clientID;

        // ✅ Fetch the latest PO ID for the client
        String poId = DatabaseService.fetchLatestPOForClient(clientID);
        if (poId == null || poId.isEmpty()) {
            throw new RuntimeException("❌ No PO ID found for Client: " + clientID);
        }

        this.purchaseWorkflowEngine = new PurchaseWorkflowEngine(clientID, poId); // ✅ Fixed: Pass poId
    }

    public void executeWorkflow(List<String> productNames) {
        productNames.forEach(productName -> {
            String poId = getPoIdForProduct(productName);
            String productType = RuleEngine.getProductType(poId);
            RuleEngine.updateProductTypeRules(poId, productType);
        });

        boolean allService = productNames.stream()
                .allMatch(productName -> "SERVICE".equalsIgnoreCase(RuleEngine.getProductType(getPoIdForProduct(productName))));

        if (allService) {
            logger.info("✅ All products are service-based, skipping Inward and moving to Invoice...");
            moveToInvoice(productNames);
        } else {
            logger.info("✅ At least one product requires Inward processing...");
            moveToInward(productNames);
        }
    }

    private void moveToInward(List<String> productNames) {
        logger.info("📦 Inward Process Started for Products: {}", productNames);
        InventoryWorkflowEngine.processInward(productNames);
    }

    private void moveToInvoice(List<String> productNames) {
        logger.info("📄 Invoice Process Started for Products: {}", productNames);
        purchaseWorkflowEngine.processInvoice(productNames); // ✅ Fixed method call
    }

    private String getPoIdForProduct(String productName) {
        return DatabaseService.fetchPoIdByProductName(productName); // ✅ Fixed: Implemented method
    }
}