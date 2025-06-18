/**
 * Represents a Terms & Conditions (T&C) template used in Purchase Orders.
 *
 * This class holds metadata for each T&C block, which is typically selected
 * at the bottom of the PO screen.
 *
 * T&C templates may vary for different companies, vendors, or departments,
 * and often include payment terms, legal conditions, or instructions.
 */
package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignore fields not mapped here during JSON parsing
public class TermsAndConditions {

    // ─── Fields ─────────────────────────────────────────────────────────────

    private String template_name;         // Name of the T&C template (e.g., "Standard PO Terms")
    private String payment_terms;         // Description of payment terms (e.g., "Within 30 days")
    private String user_firstname;        // Who created or owns this T&C template
    private String user_gid;              // User identifier (internal system ID)
    private String termsconditions_gid;   // Unique identifier for the T&C entry

    // ─── Getter & Setter for only the used field ────────────────────────────

    /**
     * Returns the name of the Terms & Conditions template.
     * This is usually displayed in the PO footer dropdown.
     *
     * @return name of the template (e.g., "Standard", "With Advance")
     */
    public String getTemplateName() {
        return template_name;
    }

    public void setTemplateName(String template_name) {
        this.template_name = template_name;
    }

    // ─── Optional: Add full accessors if other fields are needed later ──────

    public String getPaymentTerms() {
        return payment_terms;
    }

    public void setPaymentTerms(String payment_terms) {
        this.payment_terms = payment_terms;
    }

    public String getUserFirstname() {
        return user_firstname;
    }

    public void setUserFirstname(String user_firstname) {
        this.user_firstname = user_firstname;
    }

    public String getUserGid() {
        return user_gid;
    }

    public void setUserGid(String user_gid) {
        this.user_gid = user_gid;
    }

    public String getTermsconditionsGid() {
        return termsconditions_gid;
    }

    public void setTermsconditionsGid(String termsconditions_gid) {
        this.termsconditions_gid = termsconditions_gid;
    }
}