package com.Vcidex.StoryboardSystems.Common.Workflow;

import java.util.HashMap;
import java.util.Map;

public class RuleEngine {
    private static final Map<String, Boolean> rules = new HashMap<>();

    static {
        rules.put("IS_SERVICE_PRODUCT", false);
        rules.put("INWARD_REQUIRED", true);
    }

    public static boolean evaluateRule(String rule) {
        return rules.getOrDefault(rule, false);
    }
}
