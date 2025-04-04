package com.Vcidex.StoryboardSystems.Utils.Navigation;

public enum NavigationData {
    PO("Purchase", "Purchase", "Purchase Order"),
    AGREEMENT("Purchase", "Purchase", "Agreement"),
    PI("Purchase", "Purchase", "Purchase Indent");

    private final String moduleName;
    private final String menuName;
    private final String subMenuName;

    NavigationData(String moduleName, String menuName, String subMenuName) {
        this.moduleName = moduleName;
        this.menuName = menuName;
        this.subMenuName = subMenuName;
    }

    public String getModuleName() { return moduleName; }
    public String getMenuName() { return menuName; }
    public String getSubMenuName() { return subMenuName; }
}