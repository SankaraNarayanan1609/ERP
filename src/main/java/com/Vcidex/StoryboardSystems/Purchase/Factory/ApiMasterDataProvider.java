package com.Vcidex.StoryboardSystems.Purchase.Factory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;
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
                                new ObjectMapper()
                                        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                        )
                );
    }

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
            log.error("Error calling {}: {}", path, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getBranches() {
        return safeGetList(
                "/StoryboardAPI/api/SysMstBranch/BranchSummary",
                r -> r.jsonPath().getList("data.branch_name", String.class)
        );
    }

    @Override
    public List<Employee> getEmployees() {
        return safeGetList(
                "/StoryboardAPI/api/Employeelist/GetEmployeeSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("data", Employee.class)
        );
    }

    @Override
    public List<Vendor> getVendors() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstVendorRegister/GetVendorregisterSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("data", Vendor.class)
        );
    }

    @Override
    public List<Product> getProducts() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstProduct/GetProductSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("product_list", Product.class)
        );
    }

    private List<Map<String,String>> fetchCurrencyList() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstCurrency/GetPmrCurrencySummary",
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
                "/StoryboardAPI/api/PmrMstTermsConditions/GetTermsConditionsSummary",
                r -> r.then().contentType(ContentType.JSON)
                        .extract().jsonPath().getList("data.template_name", String.class)
        );
    }

    private List<Map<String,String>> fetchTaxList() {
        return safeGetList(
                "/StoryboardAPI/api/PmrMstTax/GetTaxSummary",
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