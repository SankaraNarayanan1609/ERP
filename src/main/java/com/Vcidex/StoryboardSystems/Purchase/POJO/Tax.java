/**
 * Represents tax configuration applicable to a product or vendor.
 * Tax segment helps group multiple tax types.
 */

package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tax {
    private String tax_gid;              // Unique tax identifier
    private String taxsegment_name;      // e.g., GST, VAT, etc.
    private String tax_prefix;           // Used for UI display (e.g., "GST-18%")
    private String percentage;           // Tax rate as string (e.g., "18.0")

    // ─── Getters & Setters ───
    public String getTaxPrefix() { return tax_prefix; }
    public void setTaxPrefix(String tax_prefix) { this.tax_prefix = tax_prefix; }

    public String getTaxsegment_name() { return taxsegment_name; }
    public void setTaxsegment_name(String taxsegment_name) { this.taxsegment_name = taxsegment_name; }

    public String getPercentage() { return percentage; }
    public void setPercentage(String percentage) { this.percentage = percentage; }
}