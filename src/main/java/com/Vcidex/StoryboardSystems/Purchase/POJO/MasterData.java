package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Branch;
import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;

import java.util.List;
import java.util.Map;

public class MasterData {
    public List<Branch> branches;
    public List<Employee> employees;
    public List<Vendor> vendors;
    public List<Product> products;
    public List<Tax> taxes;
    public List<TermsAndConditions> terms;
    public List<Map<String,String>> currencies;
}

