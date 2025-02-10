package com.Vcidex.StoryboardSystems.Utils.Database;

import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String CONFIG_PATH = "src/main/resources/db-config.json";
    private static String DB_URL;
    private static String USER;
    private static String PASSWORD;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            String content = new String(Files.readAllBytes(Paths.get(CONFIG_PATH)));
            JSONObject config = new JSONObject(content);
            DB_URL = config.getString("db_url");
            USER = config.getString("user");
            PASSWORD = config.getString("password");
        } catch (Exception e) {
            throw new RuntimeException("❌ Error loading database config: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }
}