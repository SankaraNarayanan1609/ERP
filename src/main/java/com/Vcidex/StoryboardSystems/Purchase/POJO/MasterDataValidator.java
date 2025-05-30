package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;

import java.util.List;
import java.util.Map;

public class MasterDataValidator {
    public static void validateMasterData(ApiMasterDataProvider provider) {
        // Branches
        List<Branch> branches = provider.getAllBranchObjects();
        assertListNotEmpty(branches, "Branch");
        logFirst(branches, "Branch", Branch::getBranchName);

        // Employees
        List<Employee> employees = provider.getEmployees();
        assertListNotEmpty(employees, "Employee");
        logFirst(employees, "Employee", Employee::getUserName);

        // Vendors
        List<Vendor> vendors = provider.getVendors();
        assertListNotEmpty(vendors, "Vendor");
        logFirst(vendors, "Vendor", Vendor::getVendor_companyname);

        // Products
        List<Product> products = provider.getProducts();
        assertListNotEmpty(products, "Product");
        logFirst(products, "Product", Product::getProductName);

        // Taxes
        List<Tax> taxes = provider.getAllTaxObjects();
        assertListNotEmpty(taxes, "Tax");
        logFirst(taxes, "Tax", Tax::getTaxPrefix);

        // Terms & Conditions
        List<TermsAndConditions> terms = provider.getAllTermsObjects();
        assertListNotEmpty(terms, "TermsAndConditions");
        logFirst(terms, "TermsAndConditions", TermsAndConditions::getTemplateName);

        // Currencies
        List<Map<String,String>> currencies = provider.getAllCurrencyObjects();
        assertListNotEmpty(currencies, "Currency");
        System.out.println("Sample Currency: " + currencies.get(0).get("currency_code"));
    }

    private static <T> void assertListNotEmpty(List<T> list, String name) {
        if (list == null || list.isEmpty()) {
            throw new RuntimeException("❌ " + name + " master not loaded or empty!");
        } else {
            System.out.println("✅ " + name + " master loaded: " + list.size() + " records");
        }
    }

    private static <T> void logFirst(List<T> list, String name, java.util.function.Function<T, String> getter) {
        if (!list.isEmpty()) {
            System.out.println("Sample " + name + ": " + getter.apply(list.get(0)));
        }
    }
}