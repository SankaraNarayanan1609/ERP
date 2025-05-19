// File: src/test/java/com/Vcidex/StoryboardSystems/Test/TestBase.java
package com.Vcidex.StoryboardSystems.Test;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.DbMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.DelegatingMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Utils.MysqlDataSourceFactory;
import org.testng.annotations.BeforeClass;

import javax.sql.DataSource;

public abstract class TestBase {
    protected static PurchaseOrderDataFactory factory;

    // Replace these with your real values
    private static final String JDBC_URL    = "jdbc:mysql://localhost:3306/yourdb";
    private static final String DB_USERNAME = "your_user";
    private static final String DB_PASSWORD = "your_pass";
    private static final String API_BASE    = "https://your-api-host";
    private static final String API_TOKEN   = "YOUR_API_TOKEN";

    @BeforeClass
    public void setUp() throws Exception {
        // 1) Instantiate your DataSource once
        DataSource ds = MysqlDataSourceFactory.create(
                JDBC_URL, DB_USERNAME, DB_PASSWORD
        );

        // 2) Build the master‐data providers using concrete types
        ApiMasterDataProvider apiProvider =
                new ApiMasterDataProvider(API_BASE, API_TOKEN);
        DbMasterDataProvider dbProvider  =
                new DbMasterDataProvider(ds);

        // 3) Delegate with the concrete providers
        DelegatingMasterDataProvider masterProvider =
                new DelegatingMasterDataProvider(apiProvider, dbProvider);

        // 4) Wire the factory
        factory = new PurchaseOrderDataFactory(masterProvider);
    }
}