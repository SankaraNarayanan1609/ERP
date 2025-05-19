package com.Vcidex.StoryboardSystems.Purchase.POJO;

import java.math.BigDecimal;

public class Product {
    private String     group;
    private String     code;
    private String     name;
    private String     description;
    private BigDecimal price;
    private BigDecimal taxRate;

    public String getGroup()         { return group; }
    public void setGroup(String g)   { this.group = g; }
    public String getCode()          { return code; }
    public void setCode(String c)    { this.code = c; }
    public String getName()          { return name; }
    public void setName(String n)    { this.name = n; }
    public String getDescription()   { return description; }
    public void setDescription(String d){ this.description = d; }
    public BigDecimal getPrice()     { return price; }
    public void setPrice(BigDecimal p){ this.price = p; }
    public BigDecimal getTaxRate()   { return taxRate; }
    public void setTaxRate(BigDecimal t){ this.taxRate = t; }
}