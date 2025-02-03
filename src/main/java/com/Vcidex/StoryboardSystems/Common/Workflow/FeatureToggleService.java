package com.Vcidex.StoryboardSystems.Common.Workflow;

import java.util.HashMap;
import java.util.Map;

public class FeatureToggleService {
    private static final Map<String, Boolean> featureToggles = new HashMap<>();

    static {
        featureToggles.put("INVOICE_APPROVAL", true);
        featureToggles.put("PO_APPROVAL", false);
    }

    public static boolean isApprovalRequired(String feature) {
        return featureToggles.getOrDefault(feature, false);
    }
}
