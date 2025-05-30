package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class TermsAndConditions {
    private String template_name;
    private String payment_terms;
    private String user_firstname;
    private String user_gid;
    private String termsconditions_gid;

    public String getTemplateName() { return template_name; }
    public void setTemplateName(String template_name) { this.template_name = template_name; }
}
