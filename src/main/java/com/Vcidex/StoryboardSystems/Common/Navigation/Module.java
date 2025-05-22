// Module.java
package com.Vcidex.StoryboardSystems.Common.Navigation;
public enum Module {
    SYSTEMS("Systems"),
    MARKETING("Marketing"),
    SALES("Sales"),
    PURCHASE("Purchase"),
    INVENTORY("Inventory"),
    PRODUCTION("Production"),
    HR("HR"),
    PAYROLL("Payroll"),
    ASSET("Asset"),
    FINANCE("Finance"),
    IT("IT");

    private final String label;
    Module(String label) { this.label = label; }
    public String getLabel() { return label; }
}
