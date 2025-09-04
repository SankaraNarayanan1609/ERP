package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {
    // Identifiers
    @JsonProperty("document_id")
    private String documentId;

    @JsonProperty("log_id")
    private String logId;

    // Product type & group
    @JsonProperty("producttype_name")
    private String productTypeName;

    @JsonProperty("productgroup_name")
    private String productGroupName;

    @JsonProperty("productgroup_code")
    private String productGroupCode;

    // Pricing & Tax
    @JsonProperty("product_price")
    private String productPrice;

    @JsonProperty("mrp_price")
    private String mrpPrice;

    @JsonProperty("cost_price")
    private String costPrice;

    // UOM details
    @JsonProperty("productuomclass_code")
    private String productUomClassCode;

    @JsonProperty("productuom_code")
    private String productUomCode;

    @JsonProperty("productuomclass_name")
    private String productUomClassName;

    // Technical flags
    @JsonProperty("stockable")
    private String stockable;

    @JsonProperty("productuom_name")
    private String productUomName;

    @JsonProperty("Status")
    private String statusString;

    @JsonProperty("serial_flag")
    private String serialFlag;

    @JsonProperty("lead_time")
    private String leadTime;

    // Display names
    @JsonProperty("product_gid")
    private String productGid;

    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("product_code")
    private String productCode;

    @JsonProperty("created_by")
    private String createdBy;

    @JsonProperty("created_date")
    private String createdDate;

    @JsonProperty("product_desc")
    private String productDesc;

    @JsonProperty("currency_code")
    private String currencyCode;

    @JsonProperty("avg_lead_time")
    private String avgLeadTime;

    @JsonProperty("purchasewarrenty_flag")
    private String purchaseWarrantyFlag;

    @JsonProperty("expirytracking_flag")
    private String expiryTrackingFlag;

    @JsonProperty("batch_flag")
    private String batchFlag;

    // Alternate names (from other APIs)
    @JsonProperty("productgroupname")
    private String productGroupnameAlt;

    @JsonProperty("producttypename")
    private String productTypeNameAlt;

    @JsonProperty("productuomclassname")
    private String productUomClassNameAlt;

    @JsonProperty("productuomname")
    private String productUomNameAlt;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("tax")
    private String tax;

    @JsonProperty("tax1")
    private String tax1;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("message")
    private String message;

    // —— NEW FIELDS FOR JSON “product_type” —— //
    @JsonIgnore
    private String productTypeRaw;   // raw JSON value of “product_type”

    /** The enum‐mapped version of the product type (resolved from names/raw). */
    private ProductType type;

    // --------------------------------------------------------------------
    // Getters for all fields
    // --------------------------------------------------------------------
    public String getDocumentId()         { return documentId; }
    public String getLogId()              { return logId; }
    public String getProductTypeName()    { return productTypeName; }
    public String getProductGroupName()   { return productGroupName; }
    public String getProductGroupCode()   { return productGroupCode; }
    public String getProductPrice()       { return productPrice; }
    public String getMrpPrice()           { return mrpPrice; }
    public String getCostPrice()          { return costPrice; }
    public String getProductUomClassCode(){ return productUomClassCode; }
    public String getProductUomCode()     { return productUomCode; }
    public String getProductUomClassName(){ return productUomClassName; }
    public String getStockable()          { return stockable; }
    public String getProductUomName()     { return productUomName; }
    public String getStatusString()       { return statusString; }
    public String getSerialFlag()         { return serialFlag; }
    public String getLeadTime()           { return leadTime; }
    public String getProductGid()         { return productGid; }
    public String getProductName()        { return productName; }
    public String getProductCode()        { return productCode; }
    public String getCreatedBy()          { return createdBy; }
    public String getCreatedDate()        { return createdDate; }
    public String getProductDesc()        { return productDesc; }
    public String getCurrencyCode()       { return currencyCode; }
    public String getAvgLeadTime()        { return avgLeadTime; }
    public String getPurchaseWarrantyFlag(){ return purchaseWarrantyFlag; }
    public String getExpiryTrackingFlag() { return expiryTrackingFlag; }
    public String getBatchFlag()          { return batchFlag; }
    public String getProductGroupnameAlt(){ return productGroupnameAlt; }
    public String getProductTypeNameAlt() { return productTypeNameAlt; }
    public String getProductUomClassNameAlt(){ return productUomClassNameAlt; }
    public String getProductUomNameAlt()  { return productUomNameAlt; }
    public String getSku()                { return sku; }
    public String getTax()                { return tax; }
    public String getTax1()               { return tax1; }
    public Boolean getStatus()            { return status; }
    public String getMessage()            { return message; }

    public BigDecimal getProductPriceDecimal() {
        try { return new BigDecimal(productPrice); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }

    public BigDecimal getCostPriceDecimal() {
        try { return new BigDecimal(costPrice); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }

    public BigDecimal getMrpPriceDecimal() {
        try { return new BigDecimal(mrpPrice); }
        catch (Exception e) { return BigDecimal.ZERO; }
    }

    // --------------------------------------------------------------------
    // Mapping for “producttype_name” / alt / raw → enum ProductType
    // --------------------------------------------------------------------

    @JsonProperty("producttype_name")
    public void setProductTypeName(String name) {
        this.productTypeName = name;
        this.type = ProductType.fromNames(this.productTypeName, this.productTypeNameAlt, this.productTypeRaw);
    }

    @JsonProperty("producttypename")
    public void setProductTypeNameAlt(String nameAlt) {
        this.productTypeNameAlt = nameAlt;
        this.type = ProductType.fromNames(this.productTypeName, this.productTypeNameAlt, this.productTypeRaw);
    }

    @JsonProperty("product_type")
    public void setProductTypeRaw(String raw) {
        this.productTypeRaw = raw;
        this.type = ProductType.fromNames(this.productTypeName, this.productTypeNameAlt, this.productTypeRaw);
    }

    @JsonIgnore
    public String getProductTypeRaw() { return productTypeRaw; }

    /** Always returns a non-null type (defaults to PHYSICAL). */
    @JsonProperty("product_type")
    public ProductType getProductType() {
        return (type == null) ? ProductType.PHYSICAL : type;
    }

    // ─── Test Helpers ─────────────────────────────────────────────────────
    public void setProductName(String productName) { this.productName = productName; }
    public void setTax(String tax)                 { this.tax = tax; }
    public void setTax1(String tax1)               { this.tax1 = tax1; }
}