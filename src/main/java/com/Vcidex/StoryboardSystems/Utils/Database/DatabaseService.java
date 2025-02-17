package com.Vcidex.StoryboardSystems.Utils.Database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class DatabaseService {//Mocked
    private static final Logger logger = LogManager.getLogger(DatabaseService.class);

    // Mocked data for simplicity
    private static final String MOCK_PO_ID = "PO001";

    /**
     * ✅ Fetches the latest PO ID for a given client (Mocked)
     */
    public static String fetchLatestPOForClient(String clientId) {
        logger.warn("⚠️ [MOCK] Bypassing DB connection for fetchLatestPOForClient");
        return MOCK_PO_ID;
    }

    /**
     * ✅ Fetches a map of pending approval clients with their request types (Mocked)
     */
    public static Map<String, List<String>> getPendingApprovalClients() {
        logger.warn("⚠️ [MOCK] Bypassing DB connection for getPendingApprovalClients");
        Map<String, List<String>> mockData = new HashMap<>();
        mockData.put("Client123", Arrays.asList("INVOICE", "PAYMENT"));
        return mockData;
    }

    /**
     * ✅ Fetches the most recently created PO (Mocked)
     */
    public static String fetchLatestPO() {
        logger.warn("⚠️ [MOCK] Bypassing DB connection for fetchLatestPO");
        return MOCK_PO_ID;
    }

    /**
     * ✅ Fetches the PO ID associated with a specific client (Mocked)
     */
    public static String fetchPoIdByClient(String clientID) {
        logger.warn("⚠️ [MOCK] Bypassing DB connection for fetchPoIdByClient");
        return MOCK_PO_ID;
    }

    /**
     * ✅ Fetches the PO ID associated with a specific product name (Mocked)
     */
    public static String fetchPoIdByProductName(String productName) {
        logger.warn("⚠️ [MOCK] Bypassing DB connection for fetchPoIdByProductName");
        return MOCK_PO_ID;
    }
}