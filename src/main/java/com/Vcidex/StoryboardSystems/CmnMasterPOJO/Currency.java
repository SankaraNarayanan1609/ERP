package com.Vcidex.StoryboardSystems.CmnMasterPOJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Currency {
    private String currencyexchange_gid;
    private String currency_code;
    private String exchange_rate;
    private String created_by;
    private String created_date;
    private String country_name;

    // Getters and setters
}
