package com.Vcidex.StoryboardSystems.Purchase.POJO;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class IndentData {
    private String branchName;
    private String requestedBy;
    @Builder.Default private String priority = "Low";
    @Builder.Default private String remarks  = "Auto-PI";

    // ✅ cost center carried in test data
    private String costCenterName;

    private LocalDate indentDate;
    private LocalDate requiredDate;

    @Builder.Default
    private List<IndentItem> items = List.of();

    // 🔹 Define the fields used in PI_Add
    @Data
    @Builder
    public static class IndentItem {
        private String productName;     // ng-select product_name
        private double quantity;        // productquantity
        private String description;     // product_remarks (optional)
        private LocalDate neededBy;     // needed_by / required_date (optional)
    }

    public static IndentDataBuilder fromCostCenter(CostCenter cc) {
        return IndentData.builder()
                .costCenterName(cc != null ? cc.getCostCenterName() : null);
    }
}