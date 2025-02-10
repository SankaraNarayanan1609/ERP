package com.Vcidex.StoryboardSystems.Utils.Database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.*;
import java.util.*;

public class DatabaseService {
    private static final Logger logger = LogManager.getLogger(DatabaseService.class);
    private static final String DB_URL = "jdbc:mysql://localhost:3306/sbs_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "password";

    public static Map<String, List<String>> getPendingApprovalClients() {
        Map<String, List<String>> clients = new HashMap<>();
        String sql = "SELECT client_id, request_type FROM approvals WHERE status = 'PENDING'";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String clientID = rs.getString("client_id");
                String requestType = rs.getString("request_type");
                clients.computeIfAbsent(clientID, k -> new ArrayList<>()).add(requestType);
            }
        } catch (SQLException e) {
            logger.error("❌ Database error fetching pending approvals: {}", e.getMessage(), e);
        }
        return clients;
    }

    public static String fetchLatestPO() {
        String sql = "SELECT po_number FROM purchase_orders ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getString("po_number");
            }
        } catch (SQLException e) {
            logger.error("❌ Database error fetching latest PO: {}", e.getMessage(), e);
        }
        return null;
    }

    public static String fetchPoIdByProductName(String productName) {
        String sql = "SELECT po_id FROM purchase_orders WHERE product_name = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, productName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("po_id");
                }
            }
        } catch (SQLException e) {
            logger.error("❌ Database error fetching PO ID for product '{}': {}", productName, e.getMessage(), e);
        }
        return null;
    }
}