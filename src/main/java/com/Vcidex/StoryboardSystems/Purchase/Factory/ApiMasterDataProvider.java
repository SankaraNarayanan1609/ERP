// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Factory/ApiMasterDataProvider.java
package com.Vcidex.StoryboardSystems.Purchase.Factory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;
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

    private final String baseUrl;
    private final String authToken;

    public ApiMasterDataProvider(String baseUrl, String authToken) {
        this.baseUrl   = baseUrl;
        this.authToken = authToken;
    }

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
            log.error("Error calling {}: {}", path, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getBranches() {
        return safeGetList(
                "/v4/api/system/SysMstBranch",
                r -> r.jsonPath().getList("data.branch_name", String.class)
        );
    }

    @Override
    public List<Employee> getEmployees() {
        return safeGetList(
                "/v4/api/system/SysMstEmployeeSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("data", Employee.class)
        );
    }

    @Override
    public List<Vendor> getVendors() {
        return safeGetList(
                "/v4/api/pmr/PmrMstVendorregister",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("data", Vendor.class)
        );
    }

    @Override
    public List<Product> getProducts() {
        return safeGetList(
                "/v4/api/pmr/ProductSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("data", Product.class)
        );
    }

    private List<Map<String,String>> fetchCurrencyList() {
        return safeGetList(
                "/v4/api/pmr/PmrMstCurrencySummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath()
                        .getObject("currency_list", new TypeRef<List<Map<String,String>>>(){})
        );
    }

    @Override
    public List<String> getCurrencies() {
        return fetchCurrencyList().stream()
                .map(m -> m.get("currency_code"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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

    @Override
    public List<String> getTermsAndConditions() {
        return safeGetList(
                "/v4/api/pmr/PmrMstTermsconditionssummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("data.template_name", String.class)
        );
    }

    private List<Map<String,String>> fetchTaxList() {
        return safeGetList(
                "/v4/api/pmr/PmrMstTaxSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getObject("data", new TypeRef<List<Map<String,String>>>(){})
        );
    }

    @Override
    public List<String> getTaxCodes() {
        return fetchTaxList().stream()
                .map(m -> m.get("tax_prefix"))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> getTaxPercentage() {
        return fetchTaxList().stream()
                .filter(m -> m.get("tax_prefix") != null && m.get("percentage") != null)
                .collect(Collectors.toMap(
                        m -> m.get("tax_prefix"),
                        m -> {
                            try { return new BigDecimal(m.get("percentage")); }
                            catch (Exception e) { return BigDecimal.ZERO; }
                        }
                ));
    }
}