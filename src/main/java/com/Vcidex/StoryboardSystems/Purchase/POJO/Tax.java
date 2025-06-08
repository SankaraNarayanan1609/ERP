package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Tax {
    private String tax_gid;
    private String taxsegment_name;
    private String tax_prefix;
    private String percentage;
    // ... all other fields

    public String getTaxPrefix() { return tax_prefix; }
    public void setTaxPrefix(String tax_prefix) { this.tax_prefix = tax_prefix; }
    public String getTaxsegment_name() { return taxsegment_name; }
    public void setTaxsegment_name(String taxsegment_name) {this.taxsegment_name = taxsegment_name;}
    public String getPercentage() { return percentage; }
    public void setPercentage(String percentage) { this.percentage = percentage; }
}