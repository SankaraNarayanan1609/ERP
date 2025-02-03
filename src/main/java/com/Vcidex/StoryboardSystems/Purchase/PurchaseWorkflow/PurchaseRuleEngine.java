package com.Vcidex.StoryboardSystems.Purchase.PurchaseWorkflow;

import com.Vcidex.StoryboardSystems.Common.Workflow.RuleEngine;

public class PurchaseRuleEngine {
    public static boolean isServiceProduct() {
        return RuleEngine.evaluateRule("IS_SERVICE_PRODUCT");
    }
}

