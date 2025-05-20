package com.Vcidex.StoryboardSystems.Utils.Config;

import javax.sql.DataSource;

import com.Vcidex.StoryboardSystems.Utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataSourceProvider {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceProvider.class);
    private static final DataSource authConnDataSource;

    static {
        // 2a. Load the three properties
        String url      = ConfigManager.getProperty("db.authconn.url", "");
        String username = ConfigManager.getProperty("db.authconn.username", "");
        String password = ConfigManager.getProperty("db.authconn.password", "");

        // 2b. Create the DataSource
        authConnDataSource = MysqlDataSourceFactory.create(url, username, password);
        logger.info("🔌 Initialized authConnDataSource for URL={}", url);
    }

    /** Returns the DataSource configured for your “AuthConn” database. */
    public static DataSource getAuthConnDataSource() {
        return authConnDataSource;
    }
}
