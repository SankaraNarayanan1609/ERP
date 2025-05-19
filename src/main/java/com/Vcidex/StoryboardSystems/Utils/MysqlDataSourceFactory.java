package com.Vcidex.StoryboardSystems.Utils;

import com.mysql.cj.jdbc.MysqlDataSource;
import javax.sql.DataSource;

/**
 * Factory for MySQL DataSource.
 */
public class MysqlDataSourceFactory {
    public static DataSource create(
            String url,
            String username,
            String password
    ) {
        MysqlDataSource ds = new MysqlDataSource();
        ds.setURL(url);
        ds.setUser(username);
        ds.setPassword(password);
        return ds;
    }
}
