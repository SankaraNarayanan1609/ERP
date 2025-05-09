package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.util.Date;
public class PurchaseOrderData {
    // Basic Fields
    private String branchName;                // Dropdown (Required)
    private String poRefNo;                   // Input
    private Date poDate;                      // Date picker (Required)
    private Date expectedDate;                // Date picker (Required)
    private String vendorName;                // Dropdown (Required)
    private String vendorDetails;             // Autofetch from Vendor selection
    private String billTo;                    // Autofetch from Branch
    private String shipTo;                    // Autofetch from Vendor
    private String requestedBy;               // Dropdown
    private String deliveryTerms;             // Input
    private String paymentTerms;              // Input
    private String requestorContactDetails;   // Input
    private String despatchMode;              // Input
    private String currency;                  // Dropdown (Required)
    private double exchangeRate;              // Autofetch from currency
    private String uploadFile;                // File upload
    private String coverNote;                 // Input
    private String renewal;                   // Radio buttons (Yes / No)
    private Date renewalDate;                 // Date picker (Required) if renewal is yes
    private String frequency;                 // Dropdown (Required) if renewal is yes

    // Terms and Conditions
    private String termsAndConditions;       // Dropdown
    private String termsAndConditionsEditor; // Input

    // Financial Fields
    private double netAmount;                 // Read-only
    private double addOnCharges;              // Input
    private double additionalDiscount;        // Input
    private double freightCharges;            // Input
    private String additionalTax;             // Dropdown or Text
    private double roundOff;                  // Input
    private double grandTotal;                // Read-only

    // Getters and Setters
    private String scenarioID;

    public String getScenarioID() {
        return scenarioID;
    }

    public void setScenarioID(String scenarioID) {
        this.scenarioID = scenarioID;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getPoRefNo() {
        return poRefNo;
    }

    public void setPoRefNo(String poRefNo) {
        this.poRefNo = poRefNo;
    }

    public Date getPoDate() {
        return poDate;
    }

    public void setPoDate(Date poDate) {
        this.poDate = poDate;
    }

    public Date getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(Date expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorDetails() {
        return vendorDetails;
    }

    public void setVendorDetails(String vendorDetails) {
        this.vendorDetails = vendorDetails;
    }

    public String getBillTo() {
        return billTo;
    }

    public void setBillTo(String billTo) {
        this.billTo = billTo;
    }

    public String getShipTo() {
        return shipTo;
    }

    public void setShipTo(String shipTo) {
        this.shipTo = shipTo;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getDeliveryTerms() {
        return deliveryTerms;
    }

    public void setDeliveryTerms(String deliveryTerms) {
        this.deliveryTerms = deliveryTerms;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getRequestorContactDetails() {
        return requestorContactDetails;
    }

    public void setRequestorContactDetails(String requestorContactDetails) {
        this.requestorContactDetails = requestorContactDetails;
    }

    public String getDespatchMode() {
        return despatchMode;
    }

    public void setDespatchMode(String despatchMode) {
        this.despatchMode = despatchMode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    // In PurchaseOrderData.java

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(String uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getCoverNote() {
        return coverNote;
    }

    public void setCoverNote(String coverNote) {
        this.coverNote = coverNote;
    }

    public String getRenewal() {
        return renewal;
    }

    public void setRenewal(String renewal) {
        this.renewal = renewal;
    }

    public Date getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(Date renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String getTermsAndConditionsEditor() {
        return termsAndConditionsEditor;
    }

    public void setTermsAndConditionsEditor(String termsAndConditionsEditor) {
        this.termsAndConditionsEditor = termsAndConditionsEditor;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(double netAmount) {
        this.netAmount = netAmount;
    }

    public double getAddOnCharges() {
        return addOnCharges;
    }

    public void setAddOnCharges(double addOnCharges) {
        this.addOnCharges = addOnCharges;
    }

    public double getAdditionalDiscount() {
        return additionalDiscount;
    }

    public void setAdditionalDiscount(double additionalDiscount) {
        this.additionalDiscount = additionalDiscount;
    }

    public double getFreightCharges() {
        return freightCharges;
    }

    public void setFreightCharges(double freightCharges) {
        this.freightCharges = freightCharges;
    }

    public String getAdditionalTax() {
        return additionalTax;
    }

    public void setAdditionalTax(String additionalTax) {
        this.additionalTax = additionalTax;
    }

    public double getRoundOff() {
        return roundOff;
    }

    public void setRoundOff(double roundOff) {
        this.roundOff = roundOff;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }
}

