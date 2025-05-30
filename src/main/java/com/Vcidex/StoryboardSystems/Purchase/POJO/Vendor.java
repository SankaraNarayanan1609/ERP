package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vendor {
    private String billing_email;
    private String tax_number;
    private String taxsegment_name;
    private String average_leadtime;
    private String region_name;
    private String address1;
    private String address2;
    private String city;
    private String contact_telephonenumber;
    private String email_id;
    private String vendorregister_gid;
    private String vendor_code;
    private String vendor_companyname;
    private String contactperson_name;
    private String region;
    private String vendor_status;
    private String active_flag;
    private String mintsoft_flag;
    private String supplier_id;
    private String vendor_gid;
    private Boolean status;
    private String message;

    // Getters and Setters

    public String getBilling_email() { return billing_email; }
    public void setBilling_email(String billing_email) { this.billing_email = billing_email; }

    public String getTax_number() { return tax_number; }
    public void setTax_number(String tax_number) { this.tax_number = tax_number; }

    public String getTaxsegment_name() { return taxsegment_name; }
    public void setTaxsegment_name(String taxsegment_name) { this.taxsegment_name = taxsegment_name; }

    public String getAverage_leadtime() { return average_leadtime; }
    public void setAverage_leadtime(String average_leadtime) { this.average_leadtime = average_leadtime; }

    public String getRegion_name() { return region_name; }
    public void setRegion_name(String region_name) { this.region_name = region_name; }

    public String getAddress1() { return address1; }
    public void setAddress1(String address1) { this.address1 = address1; }

    public String getAddress2() { return address2; }
    public void setAddress2(String address2) { this.address2 = address2; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getContact_telephonenumber() { return contact_telephonenumber; }
    public void setContact_telephonenumber(String contact_telephonenumber) { this.contact_telephonenumber = contact_telephonenumber; }

    public String getEmail_id() { return email_id; }
    public void setEmail_id(String email_id) { this.email_id = email_id; }

    public String getVendorregister_gid() { return vendorregister_gid; }
    public void setVendorregister_gid(String vendorregister_gid) { this.vendorregister_gid = vendorregister_gid; }

    public String getVendor_code() { return vendor_code; }
    public void setVendor_code(String vendor_code) { this.vendor_code = vendor_code; }

    public String getVendor_companyname() { return vendor_companyname; }
    public void setVendor_companyname(String vendor_companyname) { this.vendor_companyname = vendor_companyname; }

    public String getContactperson_name() { return contactperson_name; }
    public void setContactperson_name(String contactperson_name) { this.contactperson_name = contactperson_name; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getVendor_status() { return vendor_status; }
    public void setVendor_status(String vendor_status) { this.vendor_status = vendor_status; }

    public String getActive_flag() { return active_flag; }
    public void setActive_flag(String active_flag) { this.active_flag = active_flag; }

    public String getMintsoft_flag() { return mintsoft_flag; }
    public void setMintsoft_flag(String mintsoft_flag) { this.mintsoft_flag = mintsoft_flag; }

    public String getSupplier_id() { return supplier_id; }
    public void setSupplier_id(String supplier_id) { this.supplier_id = supplier_id; }

    public String getVendor_gid() { return vendor_gid; }
    public void setVendor_gid(String vendor_gid) { this.vendor_gid = vendor_gid; }

    public Boolean getStatus() { return status; }
    public void setStatus(Boolean status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}