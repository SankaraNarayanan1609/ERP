package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)   // <<<<---- Add here!
public class Branch {
    @JsonProperty("branch_name")
    private String branchName;

    @JsonProperty("Branch_address")
    private String branchAddress;

    @JsonProperty("City")
    private String city;

    @JsonProperty("State")
    private String state;

    @JsonProperty("Postal_code")
    private String postalCode;

    @JsonProperty("BranchStatus")
    private String branchStatus;

    // Getters & Setters

    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }

    public String getBranchAddress() { return branchAddress; }
    public void setBranchAddress(String branchAddress) { this.branchAddress = branchAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getBranchStatus() { return branchStatus; }
    public void setBranchStatus(String branchStatus) { this.branchStatus = branchStatus; }
}
