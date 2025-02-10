package com.Vcidex.StoryboardSystems.Common.Workflow;

import com.Vcidex.StoryboardSystems.Utils.Database.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class FeatureToggleService {
    private static final Logger logger = LogManager.getLogger(FeatureToggleService.class);
    private static final Map<String, List<String>> approvalChains = new HashMap<>();

    /**
     * ✅ Loads approval settings from DB for the given client.
     */
    public static void loadApprovalChainsFromDB(String clientID) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT request_type, approval_level FROM approval_settings WHERE client_id = ? ORDER BY level_order"
             )) {

            stmt.setString(1, clientID);
            ResultSet rs = stmt.executeQuery();

            Map<String, List<String>> tempChains = new HashMap<>();
            while (rs.next()) {
                String requestType = rs.getString("request_type");
                String approvalLevel = rs.getString("approval_level");

                tempChains.putIfAbsent(requestType, new ArrayList<>());
                tempChains.get(requestType).add(approvalLevel);
            }

            approvalChains.clear();
            approvalChains.putAll(tempChains);
            logger.info("✅ Approval Chains Loaded for Client [{}]: {}", clientID, approvalChains);

        } catch (Exception e) {
            logger.error("❌ Error loading approval settings from DB: {}", e.getMessage());
        }
    }

    /**
     * ✅ Checks if approval is required for a request type.
     */
    public static boolean isApprovalRequired(String clientID, String requestType) {
        if (!approvalChains.containsKey(requestType)) {
            loadApprovalChainsFromDB(clientID); // Reload if not found
        }
        return approvalChains.containsKey(requestType);
    }

    /**
     * ✅ Gets approval levels (hierarchy) for a request type.
     */
    public static List<String> getApprovalChain(String clientID, String requestType) {
        if (!approvalChains.containsKey(requestType)) {
            loadApprovalChainsFromDB(clientID);
        }
        return approvalChains.getOrDefault(requestType, List.of());
    }
}