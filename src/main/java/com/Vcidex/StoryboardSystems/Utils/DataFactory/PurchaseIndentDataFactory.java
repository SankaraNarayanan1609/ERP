// src/main/java/com/Vcidex/StoryboardSystems/Utils/DataFactory/PurchaseIndentDataFactory.java
package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.POJO.CostCenter;
import com.Vcidex.StoryboardSystems.Purchase.POJO.IndentData;

import java.time.LocalDate;
import java.util.*;

public class PurchaseIndentDataFactory {
    private final ApiMasterDataProvider api;
    private final Random rnd = new Random();

    private final List<String> branchNames;
    private final List<Employee> employees;
    private final List<CostCenter> costCenters;

    public PurchaseIndentDataFactory(ApiMasterDataProvider api) {
        this.api = api;
        this.branchNames = orEmpty(api.getBranches());
        this.employees   = orEmpty(api.getEmployees());
        this.costCenters = orEmpty(api.getCostCenters());
    }

    private static <T> List<T> orEmpty(List<T> list){ return list==null?Collections.emptyList():list; }
    private <T> T pick(List<T> list){ return list.isEmpty()? null : list.get(rnd.nextInt(list.size())); }

    private String randomEmployeeName() {
        Employee e = pick(employees);
        if (e == null || e.getUserName() == null) return "Superadmin";
        String u = e.getUserName();
        return u.contains("/") ? u.split("/")[1].trim() : u.trim();
    }

    private String randomBranchName() {
        String b = pick(branchNames);
        return b != null ? b : "HQ";
    }

    private String randomUsableCostCenterName() {
        // prefer status==true and non-blank name
        return costCenters.stream()
                .filter(cc -> Boolean.TRUE.equals(cc.getStatus()))
                .map(CostCenter::getCostCenterName)
                .filter(Objects::nonNull).map(String::trim).filter(s->!s.isEmpty())
                .findAny()
                .orElseGet(() -> {
                    CostCenter cc = pick(costCenters);
                    return cc != null ? cc.getCostCenterName() : null;
                });
    }

    /** Minimal, ready-to-use PI with a cost center prefilled. */
    public IndentData basicWithCostCenter() {
        return IndentData.builder()
                .branchName(randomBranchName())
                .requestedBy(randomEmployeeName())
                .remarks("PI with cost center")
                .costCenterName(randomUsableCostCenterName())
                .indentDate(LocalDate.now())
                .requiredDate(LocalDate.now().plusDays(7))
                .build();
    }

    /** Same but allows caller to force branch/requestor; keeps the rest random. */
    public IndentData basicWithCostCenter(String branch, String requestedBy) {
        return IndentData.builder()
                .branchName(branch != null ? branch : randomBranchName())
                .requestedBy(requestedBy != null ? requestedBy : randomEmployeeName())
                .remarks("PI with cost center")
                .costCenterName(randomUsableCostCenterName())
                .indentDate(LocalDate.now())
                .requiredDate(LocalDate.now().plusDays(7))
                .build();
    }
}