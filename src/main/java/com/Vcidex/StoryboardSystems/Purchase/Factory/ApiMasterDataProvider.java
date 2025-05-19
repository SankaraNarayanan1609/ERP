package com.Vcidex.StoryboardSystems.Purchase.Factory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;

import io.restassured.http.ContentType;
import io.restassured.common.mapper.TypeRef;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiMasterDataProvider implements MasterDataProvider {
    private final String baseUrl;
    private final String authToken;

    public ApiMasterDataProvider(String baseUrl, String authToken) {
        this.baseUrl   = baseUrl;
        this.authToken = authToken;
    }

    @Override
    public List<String> getBranches() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("/api/branches")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath().getList("data.name", String.class);
    }

    @Override
    public List<Vendor> getVendors() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("/api/vendors")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("data", Vendor.class);
    }

    @Override
    public List<Employee> getEmployees() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("/api/employees")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("data", Employee.class);
    }

    @Override
    public List<Product> getProducts() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("/api/products")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("data", Product.class);
    }

    @Override
    public List<String> getCurrencies() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("/api/currencies")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("data.code", String.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, BigDecimal> getCurrencyRates() {
        // Tell Rest-Assured exactly what generic type you want:
        List<Map<String, String>> list = given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("/api/GetOnchangeCurrency")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getObject(
                        "GetOnchangeCurrency",
                        new TypeRef<List<Map<String, String>>>() {}
                );

        Map<String, BigDecimal> rates = new HashMap<>();
        for (Map<String, String> entry : list) {
            rates.put(
                    entry.get("currency_code"),
                    new BigDecimal(entry.get("exchange_rate"))
            );
        }
        return rates;
    }

    @Override
    public List<String> getTermsAndConditions() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("/api/terms-and-conditions")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("data.code", String.class);
    }

    @Override
    public List<String> getAdditionalTaxCodes() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("/api/tax-codes")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath().getList("data.code", String.class);
    }
}