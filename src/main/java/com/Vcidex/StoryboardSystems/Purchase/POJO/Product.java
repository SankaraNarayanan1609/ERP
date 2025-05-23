package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    @JsonProperty("product_gid")
    private String gid;

    @JsonProperty("producttype_name")
    private String productTypeName;

    @JsonProperty("productgroup_name")
    private String productGroupName;

    @JsonProperty("productgroup_code")
    private String productGroupCode;

    @JsonProperty("product_code")
    private String code;

    @JsonProperty("product_name")
    private String name;

    @JsonProperty("product_desc")
    private String description;

    @JsonProperty("product_price")
    private BigDecimal productPrice;

    @JsonProperty("cost_price")
    private BigDecimal costPrice;

    // if you ever need MRP:
    @JsonProperty("mrp_price")
    private BigDecimal mrpPrice;

    @JsonProperty("tax")
    private BigDecimal tax;

    @JsonProperty("tax1")
    private BigDecimal tax1;

    // …add more fields here as you need them

    // Getters & setters

    public String getGid() { return gid; }
    public void setGid(String gid) { this.gid = gid; }

    public String getProductTypeName() { return productTypeName; }
    public void setProductTypeName(String productTypeName) { this.productTypeName = productTypeName; }

    public String getProductGroupName() { return productGroupName; }
    public void setProductGroupName(String productGroupName) { this.productGroupName = productGroupName; }

    public String getProductGroupCode() { return productGroupCode; }
    public void setProductGroupCode(String productGroupCode) { this.productGroupCode = productGroupCode; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getProductPrice() { return productPrice; }
    public void setProductPrice(BigDecimal productPrice) { this.productPrice = productPrice; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getMrpPrice() { return mrpPrice; }
    public void setMrpPrice(BigDecimal mrpPrice) { this.mrpPrice = mrpPrice; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getTax1() { return tax1; }
    public void setTax1(BigDecimal tax1) { this.tax1 = tax1; }
}