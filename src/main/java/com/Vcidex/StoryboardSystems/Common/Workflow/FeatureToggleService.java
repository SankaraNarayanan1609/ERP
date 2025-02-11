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

    public static boolean isApprovalRequired(String clientID, String requestType) {
        return approvalChains.containsKey(requestType);
    }

    public static List<String> getApprovalChain(String clientID, String requestType) {
        return approvalChains.getOrDefault(requestType, List.of("DEFAULT_APPROVAL"));
    }
}