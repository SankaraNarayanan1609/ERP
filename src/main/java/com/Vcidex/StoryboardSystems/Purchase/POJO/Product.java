package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    @JsonProperty("" +
            "")
    private String documentId;

    @JsonProperty("log_id")
    private String logId;

    @JsonProperty("producttype_name")
    private String productTypeName;

    @JsonProperty("productgroup_name")
    private String productGroupName;

    @JsonProperty("productgroup_code")
    private String productGroupCode;

    @JsonProperty("product_price")
    private String productPrice;

    @JsonProperty("mrp_price")
    private String mrpPrice;

    @JsonProperty("cost_price")
    private String costPrice;

    @JsonProperty("productuomclass_code")
    private String productUomClassCode;

    @JsonProperty("productuom_code")
    private String productUomCode;

    @JsonProperty("productuomclass_name")
    private String productUomClassName;

    @JsonProperty("stockable")
    private String stockable;

    @JsonProperty("productuom_name")
    private String productUomName;

    @JsonProperty("product_type")
    private String productType;

    @JsonProperty("Status")
    private String statusString;

    @JsonProperty("serial_flag")
    private String serialFlag;

    @JsonProperty("lead_time")
    private String leadTime;

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
    private Boolean status; // Boolean field

    @JsonProperty("message")
    private String message;

    // Getters and Setters for ALL fields

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getLogId() { return logId; }
    public void setLogId(String logId) { this.logId = logId; }

    public String getProductTypeName() { return productTypeName; }
    public void setProductTypeName(String productTypeName) { this.productTypeName = productTypeName; }

    public String getProductGroupName() { return productGroupName; }
    public void setProductGroupName(String productGroupName) { this.productGroupName = productGroupName; }

    public String getProductGroupCode() { return productGroupCode; }
    public void setProductGroupCode(String productGroupCode) { this.productGroupCode = productGroupCode; }

    public String getProductPrice() { return productPrice; }
    public void setProductPrice(String productPrice) { this.productPrice = productPrice; }

    public String getMrpPrice() { return mrpPrice; }
    public void setMrpPrice(String mrpPrice) { this.mrpPrice = mrpPrice; }

    public String getCostPrice() { return costPrice; }
    public void setCostPrice(String costPrice) { this.costPrice = costPrice; }

    public String getProductUomClassCode() { return productUomClassCode; }
    public void setProductUomClassCode(String productUomClassCode) { this.productUomClassCode = productUomClassCode; }

    public String getProductUomCode() { return productUomCode; }
    public void setProductUomCode(String productUomCode) { this.productUomCode = productUomCode; }

    public String getProductUomClassName() { return productUomClassName; }
    public void setProductUomClassName(String productUomClassName) { this.productUomClassName = productUomClassName; }

    public String getStockable() { return stockable; }
    public void setStockable(String stockable) { this.stockable = stockable; }

    public String getProductUomName() { return productUomName; }
    public void setProductUomName(String productUomName) { this.productUomName = productUomName; }

    public String getProductType() { return productType; }
    public void setProductType(String productType) { this.productType = productType; }

    public String getStatusString() { return statusString; }
    public void setStatusString(String statusString) { this.statusString = statusString; }

    public String getSerialFlag() { return serialFlag; }
    public void setSerialFlag(String serialFlag) { this.serialFlag = serialFlag; }

    public String getLeadTime() { return leadTime; }
    public void setLeadTime(String leadTime) { this.leadTime = leadTime; }

    public String getProductGid() { return productGid; }
    public void setProductGid(String productGid) { this.productGid = productGid; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getProductDesc() { return productDesc; }
    public void setProductDesc(String productDesc) { this.productDesc = productDesc; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getAvgLeadTime() { return avgLeadTime; }
    public void setAvgLeadTime(String avgLeadTime) { this.avgLeadTime = avgLeadTime; }

    public String getPurchaseWarrantyFlag() { return purchaseWarrantyFlag; }
    public void setPurchaseWarrantyFlag(String purchaseWarrantyFlag) { this.purchaseWarrantyFlag = purchaseWarrantyFlag; }

    public String getExpiryTrackingFlag() { return expiryTrackingFlag; }
    public void setExpiryTrackingFlag(String expiryTrackingFlag) { this.expiryTrackingFlag = expiryTrackingFlag; }

    public String getBatchFlag() { return batchFlag; }
    public void setBatchFlag(String batchFlag) { this.batchFlag = batchFlag; }

    public String getProductGroupnameAlt() { return productGroupnameAlt; }
    public void setProductGroupnameAlt(String productGroupnameAlt) { this.productGroupnameAlt = productGroupnameAlt; }

    public String getProductTypeNameAlt() { return productTypeNameAlt; }
    public void setProductTypeNameAlt(String productTypeNameAlt) { this.productTypeNameAlt = productTypeNameAlt; }

    public String getProductUomClassNameAlt() { return productUomClassNameAlt; }
    public void setProductUomClassNameAlt(String productUomClassNameAlt) { this.productUomClassNameAlt = productUomClassNameAlt; }

    public String getProductUomNameAlt() { return productUomNameAlt; }
    public void setProductUomNameAlt(String productUomNameAlt) { this.productUomNameAlt = productUomNameAlt; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getTax() { return tax; }
    public void setTax(String tax) { this.tax = tax; }

    public String getTax1() { return tax1; }
    public void setTax1(String tax1) { this.tax1 = tax1; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    // Utility methods for number conversion
    public BigDecimal getProductPriceDecimal() {
        try { return new BigDecimal(productPrice); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    public BigDecimal getCostPriceDecimal() {
        try { return new BigDecimal(costPrice); } catch (Exception e) { return BigDecimal.ZERO; }
    }

    public BigDecimal getMrpPriceDecimal() {
        try { return new BigDecimal(mrpPrice); } catch (Exception e) { return BigDecimal.ZERO; }
    }
}