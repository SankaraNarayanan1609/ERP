package com.Vcidex.StoryboardSystems.Inventory.InventoryWorkflow;

import com.Vcidex.StoryboardSystems.Common.Workflow.RuleEngine;

public class InventoryRuleEngine {
    public static boolean isStockAvailable() {
        return RuleEngine.evaluateRule("STOCK_AVAILABLE");
    }
}
