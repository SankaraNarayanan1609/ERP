package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CostCenter {

    // Core identifiers
    @JsonProperty("costcenter_gid")
    private String costCenterGid;

    @JsonProperty("costcenter_code")
    private String costCenterCode;

    @JsonProperty("costcenter_name")
    private String costCenterName;

    // Financials (string in JSON, convertable to BigDecimal)
    @JsonProperty("budget_allocated")
    private String budgetAllocated;

    @JsonProperty("amount_used")
    private String amountUsed;

    @JsonProperty("available")
    private String available;

    @JsonProperty("provisional_amount")
    private String provisionalAmount;

    @JsonProperty("available_budget")
    private String availableBudget;

    // Status flags
    @JsonProperty("Status")
    private String statusString;

    @JsonProperty("status")
    private Boolean status;

    @JsonProperty("message")
    private String message;

    // Extra API aliases (nullable in your sample)
    @JsonProperty("CostCenter_Code")
    private String costCenterCodeAlt;

    @JsonProperty("CostCenter_Name")
    private String costCenterNameAlt;

    @JsonProperty("Budget")
    private String budgetAlt;

    @JsonProperty("StatusEdit")
    private String statusEdit;

    @JsonProperty("CostCenter_CodeEdit")
    private String costCenterCodeEdit;

    @JsonProperty("CostCenter_NameEdit")
    private String costCenterNameEdit;

    @JsonProperty("BudgetEdit")
    private String budgetEdit;

    // ─── Getters ───────────────────────────────────────────
    public String getCostCenterGid()       { return costCenterGid; }
    public String getCostCenterCode()      { return costCenterCode; }
    public String getCostCenterName()      { return costCenterName; }
    public String getBudgetAllocated()     { return budgetAllocated; }
    public String getAmountUsed()          { return amountUsed; }
    public String getAvailable()           { return available; }
    public String getProvisionalAmount()   { return provisionalAmount; }
    public String getAvailableBudget()     { return availableBudget; }
    public String getStatusString()        { return statusString; }
    public Boolean getStatus()             { return status; }
    public String getMessage()             { return message; }
    public String getCostCenterCodeAlt()   { return costCenterCodeAlt; }
    public String getCostCenterNameAlt()   { return costCenterNameAlt; }
    public String getBudgetAlt()           { return budgetAlt; }
    public String getStatusEdit()          { return statusEdit; }
    public String getCostCenterCodeEdit()  { return costCenterCodeEdit; }
    public String getCostCenterNameEdit()  { return costCenterNameEdit; }
    public String getBudgetEdit()          { return budgetEdit; }

    // ─── Helpers to get safe decimals ──────────────────────
    public BigDecimal getBudgetAllocatedDecimal() {
        return toDecimal(budgetAllocated);
    }

    public BigDecimal getAmountUsedDecimal() {
        return toDecimal(amountUsed);
    }

    public BigDecimal getAvailableDecimal() {
        return toDecimal(available);
    }

    public BigDecimal getProvisionalAmountDecimal() {
        return toDecimal(provisionalAmount);
    }

    public BigDecimal getAvailableBudgetDecimal() {
        return toDecimal(availableBudget);
    }

    // ─── Internal conversion helper ────────────────────────
    @JsonIgnore
    private BigDecimal toDecimal(String val) {
        if (val == null) return BigDecimal.ZERO;
        try {
            // Strip commas from "200,000.00"
            return new BigDecimal(val.replace(",", ""));
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}