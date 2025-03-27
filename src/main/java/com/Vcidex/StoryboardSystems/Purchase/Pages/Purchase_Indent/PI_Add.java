package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PI_Add extends BasePage {

    // Locators
    private By branchNameDropdown = By.id("branch-dropdown");
    private By refNoField = By.id("ref-no-field");
    private By departmentField = By.id("department-field");
    private By requestedByDropdown = By.id("requested-by-dropdown");
    private By priorityHighRadio = By.id("priority-high-radio");
    private By priorityLowRadio = By.id("priority-low-radio");
    private By remarksField = By.id("remarks-field");
    private By productNameDropdown = By.id("product-name-dropdown");
    private By productGroupField = By.id("product-group-field");
    private By productCodeField = By.id("product-code-field");
    private By productDescriptionField = By.id("product-description-field");
    private By unitField = By.id("unit-field");
    private By quantityRequestedField = By.id("quantity-requested-field");
    private By addButton = By.id("add-button");

    // Constructor
    public PI_Add(WebDriver driver) {
        super(driver);
    }

    // Select Branch
    public void selectBranchName(String branchName) {
        selectDropdownUsingVisibleText(branchNameDropdown, branchName);
    }

    // Get Ref No (Auto-generated)
    public String getRefNo() {
        return getText(refNoField);
    }

    // Get Department (Auto-fetched)
    public String getDepartment() {
        return getText(departmentField);
    }

    // Select Requested By
    public void selectRequestedBy(String requestedBy) {
        selectDropdownUsingVisibleText(requestedByDropdown, requestedBy);
    }

    // Select Priority (High/Low)
    public void selectPriority(String priority) {
        if ("high".equalsIgnoreCase(priority)) {
            click(priorityHighRadio);
        } else if ("low".equalsIgnoreCase(priority)) {
            click(priorityLowRadio);
        }
    }

    // Enter Remarks
    public void enterRemarks(String remarks) {
        sendKeys(remarksField, remarks);
    }

    // Select Product Name
    public void selectProductName(String productName) {
        selectDropdownUsingVisibleText(productNameDropdown, productName);
        fetchProductDetails();
    }

    // Fetch Product Details (Auto-fetched)
    private void fetchProductDetails() {
        System.out.println("Product Group: " + getText(productGroupField));
        System.out.println("Product Code: " + getText(productCodeField));
        System.out.println("Product Description: " + getText(productDescriptionField));
        System.out.println("Unit: " + getText(unitField));
    }

    // Enter Quantity Requested
    public void enterQuantityRequested(String quantity) {
        sendKeys(quantityRequestedField, quantity);
    }

    // Click Add Button
    public void clickAddButton() {
        click(addButton);
    }
}