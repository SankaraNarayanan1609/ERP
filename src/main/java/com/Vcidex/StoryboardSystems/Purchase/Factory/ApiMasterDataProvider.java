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
import java.util.stream.Collectors;

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
                .get("https://erplite.storyboarderp.com/v4/api/system/SysMstBranch")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                // each branch object has a "branch_name" field:
                .jsonPath().getList("data.branch_name", String.class);
    }
    @Override
    public List<Employee> getEmployees() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("https://erplite.storyboarderp.com/v4/api/system/SysMstEmployeeSummary")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                // employee POJO fields (user_gid, user_name, employee_joiningdate…)
                .extract().jsonPath().getList("data", Employee.class);
    }
    @Override
    public List<Vendor> getVendors() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("https://erplite.storyboarderp.com/v4/api/pmr/PmrMstVendorregister")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                // vendor POJO should have matching fields (vendor_code, vendor_companyname, etc.)
                .extract().jsonPath().getList("data", Vendor.class);
    }
    @Override
    public List<Product> getProducts() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("https://erplite.storyboarderp.com/v4/api/pmr/ProductSummary")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                // product POJO fields should line up with your JSON (product_code, product_name…)
                .extract().jsonPath().getList("data", Product.class);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> fetchCurrencyList() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("https://erplite.storyboarderp.com/v4/api/pmr/PmrMstCurrencySummary")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                // the top-level array is named "currency_list"
                .extract()
                .jsonPath()
                .getObject(
                        "currency_list",
                        new TypeRef<List<Map<String, String>>>() {}
                );
    }

    @Override
    public List<String> getCurrencies() {
        return fetchCurrencyList().stream()
                .map(m -> m.get("currency_code"))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, BigDecimal> getCurrencyRates() {
        return fetchCurrencyList().stream()
                .collect(Collectors.toMap(
                        m -> m.get("currency_code"),
                        m -> new BigDecimal(m.get("exchange_rate"))
                ));
    }

    @Override
    public List<String> getTermsAndConditions() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("https://erplite.storyboarderp.com/v4/api/pmr/PmrMstTermsconditionssummary")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                // your sample JSON uses "template_name" for each item
                .extract().jsonPath().getList("data.template_name", String.class);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, String>> fetchTaxList() {
        return given()
                .baseUri(baseUrl)
                .auth().oauth2(authToken)
                .when()
                .get("https://erplite.storyboarderp.com/v4/api/pmr/PmrMstTaxSummary")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getObject(
                        "data",
                        new TypeRef<List<Map<String, String>>>() {}
                );
    }

    @Override
    public List<String> getTaxCodes() {
        return fetchTaxList().stream()
                .map(m -> m.get("tax_prefix"))
                .collect(Collectors.toList());
    }

    @Override//Method does not override method from its superclass
    public Map<String, BigDecimal> getTaxPercentage() { // 'getTaxPercentage()' in 'com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider' clashes with 'getTaxPercentage()' in 'com.Vcidex.StoryboardSystems.Purchase.Factory.MasterDataProvider'; attempting to use incompatible return type
        return fetchTaxList().stream()
                .collect(Collectors.toMap(
                        m -> m.get("tax_prefix"),
                        m -> new BigDecimal(m.get("percentage"))
                ));
    }

}