/**
 * Represents a company branch, typically used for filtering purchase orders,
 * assigning employees, and populating dropdowns in forms.
 *
 * This class is deserialized from JSON response using Jackson.
 */

package com.Vcidex.StoryboardSystems.CmnMasterPOJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignores extra fields in JSON that are not mapped
public class Branch {

    @JsonProperty("branch_name") // Maps JSON key "branch_name" to this variable
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

    // ────────────────────────────────────────────────────────────────────────
    // Getter and Setter Methods — used to access private fields from other classes

    /**
     * Gets the name of the branch (e.g., "Chennai HO").
     */
    public String getBranchName() { return branchName; }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    /**
     * Gets the address of the branch (street, area, etc.).
     */
    public String getBranchAddress() { return branchAddress; }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

    /**
     * Gets the city where the branch is located.
     */
    public String getCity() { return city; }

    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state of the branch's location (e.g., Tamil Nadu).
     */
    public String getState() { return state; }

    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the PIN/postal code (used for mailing and geo).
     */
    public String getPostalCode() { return postalCode; }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Returns the status of the branch (e.g., "Active", "Inactive").
     */
    public String getBranchStatus() { return branchStatus; }

    public void setBranchStatus(String branchStatus) {
        this.branchStatus = branchStatus;
    }
}