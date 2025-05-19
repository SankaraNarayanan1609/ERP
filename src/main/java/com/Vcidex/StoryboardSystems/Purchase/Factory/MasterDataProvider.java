package com.Vcidex.StoryboardSystems.Purchase.Factory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface MasterDataProvider {
    List<String>            getBranches();
    List<Vendor>            getVendors();
    List<Employee>          getEmployees();
    List<Product>           getProducts();
    List<String>            getCurrencies();
    List<String>            getTermsAndConditions();
    List<String>            getAdditionalTaxCodes();

    /**
     * currencyCode → exchangeRate
     * pulled directly from your currency_master table
     */
    Map<String, BigDecimal> getCurrencyRates();
}