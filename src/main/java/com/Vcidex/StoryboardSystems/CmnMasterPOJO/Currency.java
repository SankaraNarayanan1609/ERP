/**
 * Represents a currency used in purchase orders and invoice workflows.
 * This includes exchange rate, currency code (like INR/USD), and related metadata.
 */

package com.Vcidex.StoryboardSystems.CmnMasterPOJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignores unknown fields during JSON parsing
public class Currency {

    private String currencyexchange_gid;  // Unique identifier for the currency rate config
    private String currency_code;         // e.g., INR, USD, EUR
    private String exchange_rate;         // Rate compared to base currency (e.g., 1 USD = 83 INR)
    private String created_by;            // Audit field: who configured the entry
    private String created_date;          // When the exchange rate was added
    private String country_name;          // Country associated with this currency

    // ─── Getter and Setter Methods ───
    public String getCurrencyexchange_gid() { return currencyexchange_gid; }
    public void setCurrencyexchange_gid(String currencyexchange_gid) { this.currencyexchange_gid = currencyexchange_gid; }

    public String getCurrency_code() { return currency_code; }
    public void setCurrency_code(String currency_code) { this.currency_code = currency_code; }

    public String getExchange_rate() { return exchange_rate; }
    public void setExchange_rate(String exchange_rate) { this.exchange_rate = exchange_rate; }

    public String getCreated_by() { return created_by; }
    public void setCreated_by(String created_by) { this.created_by = created_by; }

    public String getCreated_date() { return created_date; }
    public void setCreated_date(String created_date) { this.created_date = created_date; }

    public String getCountry_name() { return country_name; }
    public void setCountry_name(String country_name) { this.country_name = country_name; }
}
