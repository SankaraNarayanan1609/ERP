package com.Vcidex.StoryboardSystems.Purchase.Factory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Defines methods to fetch master data for the Purchase module.
 */
public interface MasterDataProvider {
    /** List of branch names */
    List<String> getBranches();

    /** Full vendor objects */
    List<Vendor> getVendors();

    /** Full employee objects */
    List<Employee> getEmployees();

    /** Full product objects */
    List<Product> getProducts();

    /** Currency codes, e.g. ["USD","INR",…] */
    List<String> getCurrencies();

    /** currencyCode → exchangeRate */
    Map<String, BigDecimal> getCurrencyRates();

    /** Terms & Conditions templates */
    List<String> getTermsAndConditions();

    /** Tax prefixes, e.g. ["SGST 9%","CGST 9%",…] */
    List<String> getTaxCodes();

    /** tax_prefix → percentage, e.g. {"SGST 9%"→9.00,…} */
    Map<String, BigDecimal> getTaxPercentage();
}