package com.Vcidex.StoryboardSystems.Purchase.POJO;

public class Vendor {
    private long   id;
    private String name;
    private String details;
    private String branchAddress;
    private String shippingAddress;


    public long getId()            { return id; }
    public void setId(long id)     { this.id = id; }
    public String getName()        { return name; }
    public void setName(String n)  { this.name = n; }
    public String getDetails()     { return details; }
    public void setDetails(String d){ this.details = d; }
    public String getBranchAddress(){ return branchAddress; }
    public void setBranchAddress(String b){ this.branchAddress = b; }
    public String getShippingAddress(){ return shippingAddress; }
    public void setShippingAddress(String s){ this.shippingAddress = s; }
}