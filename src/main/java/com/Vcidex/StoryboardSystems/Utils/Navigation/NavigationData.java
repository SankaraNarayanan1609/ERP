package com.Vcidex.StoryboardSystems.Utils.Navigation;

public enum NavigationData {

    // Purchase Module
    DIRECT_PO("Purchase", "Purchase", "Purchase Order"),
    RAISE_PO("Purchase", "Purchase", "Purchase Order"),
    RAISE_PI("Purchase", "Purchase", "Purchase Indent");

    // Sales Module (Commented for now)
    // DIRECT_SALES("Sales", "Sales Management", "Direct Sales"),
    // SALES_INVOICE("Sales", "Sales Management", "Sales Invoice");

    private final String moduleName;
    private final String menuName;
    private final String subMenuName;

    NavigationData(String moduleName, String menuName, String subMenuName) {
        this.moduleName = moduleName;
        this.menuName = menuName;
        this.subMenuName = subMenuName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getSubMenuName() {
        return subMenuName;
    }
}
