package com.Vcidex.StoryboardSystems.Purchase.Factory;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Branch;
import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.common.mapper.TypeRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;

public class ApiMasterDataProvider implements MasterDataProvider {

    private static final Logger log = LoggerFactory.getLogger(ApiMasterDataProvider.class);

    static {
        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(new ObjectMapperConfig()
                        .jackson2ObjectMapperFactory((cls, charset) ->
                                new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                        )
                );
    }

    private final String baseUrl;
    private final String authToken;

    public ApiMasterDataProvider(String baseUrl, String authToken) {
        // Defensive checks for base URL and token
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new IllegalArgumentException("Base URL cannot be null or empty.");
        }
        this.baseUrl = baseUrl;

        // Remove the "Bearer " prefix if present in the token
        if (authToken != null && authToken.startsWith("Bearer ")) {
            this.authToken = authToken.substring("Bearer ".length());
        } else if (authToken != null) {
            this.authToken = authToken;
        } else {
            throw new IllegalArgumentException("Auth token cannot be null or empty.");
        }
    }

    // Getter methods
    public String getBaseUrl() {
        return baseUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    // --- Shared Utility for all API list fetches ---
    private <T> List<T> safeGetList(String path, Function<Response, List<T>> extractor) {
        try {
            Response resp = given()
                    .baseUri(baseUrl)
                    .auth().oauth2(authToken)
                    .when()
                    .get(path);

            if (resp.statusCode() != 200) {
                log.warn("GET {} returned status {} → empty list", path, resp.statusCode());
                return Collections.emptyList();
            }
            return extractor.apply(resp);
        } catch (Exception e) {
            log.error("Error calling {}: {}", path, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // --- BRANCHES ---
    @Override
    public List<String> getBranches() {
        List<Branch> allBranches = getAllBranchObjects();
        return allBranches.stream()
                .map(Branch::getBranchName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Branch> getAllBranchObjects() {
        return safeGetList(
                "/StoryboardAPI/api/SysMstBranch/BranchSummary",
                r -> r.jsonPath().getList("branch_list1", Branch.class)
        );
    }

    // --- EMPLOYEES ---
    @Override
    public List<Employee> getEmployees() {
        List<Employee> employees = safeGetList(
                "/StoryboardAPI/api/Employeelist/GetEmployeeSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("Getemployee_lists", Employee.class)
        );
        log.info("Fetched and mapped {} employees into POJO class.", employees.size());
        for (Employee emp : employees) {
            log.debug("Employee POJO: {}", emp);
        }
        return employees;
    }

    // --- VENDORS ---
    @Override
    public List<Vendor> getVendors() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstVendorRegister/GetVendorregisterSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("Getvendor_lists", Vendor.class)
        );
    }

    // --- PRODUCTS ---
    @Override
    public List<Product> getProducts() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstProduct/GetProductSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("product_list", Product.class)
        );
    }

    // --- CURRENCIES ---
    @Override
    public List<String> getCurrencies() {
        return getAllCurrencyObjects().stream()
                .map(m -> m.get("currency_code"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // Full currency objects (as Map) for more details (for MasterData.allMasters)
    public List<Map<String, String>> getAllCurrencyObjects() {
        return fetchCurrencyList();
    }

    // Helper for currency
    private List<Map<String, String>> fetchCurrencyList() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstCurrency/GetPmrCurrencySummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getObject("currency_list", new TypeRef<List<Map<String, String>>>() {})
        );
    }

    @Override
    public Map<String, BigDecimal> getCurrencyRates() {
        return fetchCurrencyList().stream()
                .filter(m -> m.get("currency_code") != null && m.get("exchange_rate") != null)
                .collect(Collectors.toMap(
                        m -> m.get("currency_code"),
                        m -> {
                            try { return new BigDecimal(m.get("exchange_rate")); }
                            catch (Exception e) { return BigDecimal.ZERO; }
                        }
                ));
    }

    // --- TERMS AND CONDITIONS ---
    public List<TermsAndConditions> getAllTermsObjects() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstTermsConditions/GetTermsConditionsSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("Gettemplate_list", TermsAndConditions.class)
        );
    }

    @Override
    public List<String> getTermsAndConditions() {
        return getAllTermsObjects().stream()
                .map(TermsAndConditions::getTemplateName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    // --- TAX ---
    private List<Tax> fetchTaxList() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstTax/GetTaxSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("pmrtax_list", Tax.class)
        );
    }

    public List<Tax> getAllTaxObjects() {
        return fetchTaxList();
    }

    @Override
    public List<String> getTaxCodes() {
        List<Tax> taxes = fetchTaxList();
        List<String> codes = taxes.stream()
                .map(Tax::getTaxPrefix)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        System.out.println("DEBUG: Tax codes = " + codes);
        return codes;
    }

    @Override
    public Map<String, BigDecimal> getTaxPercentage() {
        return fetchTaxList().stream()
                .filter(m -> m.getTaxPrefix() != null && m.getPercentage() != null)
                .collect(Collectors.toMap(
                        Tax::getTaxPrefix,
                        m -> {
                            try {
                                return new BigDecimal(m.getPercentage().trim());
                            } catch (Exception e) {
                                return BigDecimal.ZERO;
                            }
                        }
                ));
    }
}