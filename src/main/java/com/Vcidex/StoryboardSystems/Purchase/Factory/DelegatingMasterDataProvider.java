package com.Vcidex.StoryboardSystems.Purchase.Factory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class DelegatingMasterDataProvider implements MasterDataProvider {
    private final MasterDataProvider api;
    private final MasterDataProvider db;

    public DelegatingMasterDataProvider(
            ApiMasterDataProvider api,
            DbMasterDataProvider db
    ) {
        this.api = api;
        this.db  = db;
    }

    @Override
    public List<String> getBranches() {
        try {
            return api.getBranches();
        } catch (Exception e) {
            return db.getBranches();
        }
    }

    @Override
    public List<Vendor> getVendors() {
        try {
            return api.getVendors();
        } catch (Exception e) {
            return db.getVendors();
        }
    }

    @Override
    public List<Employee> getEmployees() {
        try {
            return api.getEmployees();
        } catch (Exception e) {
            return db.getEmployees();
        }
    }

    @Override
    public List<Product> getProducts() {
        try {
            return api.getProducts();
        } catch (Exception e) {
            return db.getProducts();
        }
    }

    @Override
    public List<String> getCurrencies() {
        try {
            return api.getCurrencies();
        } catch (Exception e) {
            return db.getCurrencies();
        }
    }

    @Override
    public Map<String, BigDecimal> getCurrencyRates() {
        try {
            return api.getCurrencyRates();
        } catch (Exception e) {
            return db.getCurrencyRates();
        }
    }

    @Override
    public List<String> getTermsAndConditions() {
        try {
            return api.getTermsAndConditions();
        } catch (Exception e) {
            return db.getTermsAndConditions();
        }
    }

    @Override
    public List<String> getAdditionalTaxCodes() {
        try {
            return api.getAdditionalTaxCodes();
        } catch (Exception e) {
            return db.getAdditionalTaxCodes();
        }
    }
}