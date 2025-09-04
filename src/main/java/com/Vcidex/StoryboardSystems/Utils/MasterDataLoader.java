package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.POJO.MasterData;
import com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger;

public class MasterDataLoader {
    private final ApiMasterDataProvider api;

    public MasterDataLoader(String baseUrl, String token) {
        this.api = new ApiMasterDataProvider(baseUrl, token);
    }

    /**
     * Fetches all the master-data via the API and
     * throws if any collection is empty.
     */
    public MasterData loadAndValidate() {
        MasterData md = new MasterData();
        try {
            md.branches   = api.getAllBranchObjects();
            md.employees  = api.getEmployees();
            md.vendors    = api.getVendors();
            md.products   = api.getProducts();
            md.taxes      = api.getAllTaxObjects();
            md.terms      = api.getAllTermsObjects();
            md.currencies = api.getAllCurrencyObjects();

            // simple null or empty checks
            if (md.branches.isEmpty())   throw new IllegalStateException("No branches");
            if (md.employees.isEmpty())  throw new IllegalStateException("No employees");
            if (md.vendors.isEmpty())    throw new IllegalStateException("No vendors");
            if (md.products.isEmpty())   throw new IllegalStateException("No products");
            if (md.taxes.isEmpty())      throw new IllegalStateException("No taxes");
            if (md.terms.isEmpty())      throw new IllegalStateException("No terms");
            if (md.currencies.isEmpty()) throw new IllegalStateException("No currencies");

            return md;
        } catch (Exception e) {
            // wrap or rethrow so test setup will fail fast
            com.Vcidex.StoryboardSystems.Utils.Logger.DiagnosticsLogger
                    .onFailure(null, "MasterDataLoader.loadAndValidate", e);
            throw e;
        }
    }
}