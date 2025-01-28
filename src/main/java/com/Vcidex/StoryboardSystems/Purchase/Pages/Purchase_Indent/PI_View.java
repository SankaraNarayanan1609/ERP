package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class PI_View extends BasePage {

    // Locators for the PI_View page elements
    private By refNoField = By.id("refNo");  // Reference Number (display only)
    private By departmentField = By.id("department");  // Department (display only)
    private By requestedByField = By.id("requestedBy");  // Requested By (display only)
    private By priorityField = By.id("priority");  // Priority (display only)
    private By remarksField = By.id("remarks");  // Remarks (display only)
    private By productNameField = By.id("productName");  // Product Name (display only)
    private By productGroupField = By.id("productGroup");  // Product Group (display only)
    private By productCodeField = By.id("productCode");  // Product Code (display only)
    private By productDescriptionField = By.id("productDescription");  // Product Description (display only)
    private By unitField = By.id("unit");  // Unit (display only)
    private By quantityRequestedField = By.id("quantityRequested");  // Quantity Requested (display only)
    private By closeButton = By.id("addButton");  // Add Button (optional, depends on UI)

    // Constructor to initialize WebDriver
    public PI_View(WebDriver driver) {
        super(driver);
    }

    // Method to get the Reference Number from the page (PI View)
    public String getRefNo() {
        return getElementText(refNoField);  // Return the reference number text
    }

    // Method to get the Department from the page (PI View)
    public String getDepartment() {
        return getElementText(departmentField);  // Return the department text
    }

    // Method to get the Requested By field from the page (PI View)
    public String getRequestedBy() {
        return getElementText(requestedByField);  // Return the requested by text
    }

    // Method to get the Priority from the page (PI View)
    public String getPriority() {
        return getElementText(priorityField);  // Return the priority text
    }

    // Method to get the Remarks from the page (PI View)
    public String getRemarks() {
        return getElementText(remarksField);  // Return the remarks text
    }

    // Method to get the Product Name from the page (PI View)
    public String getProductName() {
        return getElementText(productNameField);  // Return the product name text
    }

    // Method to get the Product Group from the page (PI View)
    public String getProductGroup() {
        return getElementText(productGroupField);  // Return the product group text
    }

    // Method to get the Product Code from the page (PI View)
    public String getProductCode() {
        return getElementText(productCodeField);  // Return the product code text
    }

    // Method to get the Product Description from the page (PI View)
    public String getProductDescription() {
        return getElementText(productDescriptionField);  // Return the product description text
    }

    // Method to get the Unit from the page (PI View)
    public String getUnit() {
        return getElementText(unitField);  // Return the unit text
    }

    // Method to get the Quantity Requested from the page (PI View)
    public String getQuantityRequested() {
        return getElementText(quantityRequestedField);  // Return the quantity requested text
    }

    // Optional: Method to click the "Add" button (if any action is required in the PI View page)
    public void clickCloseButton() {
        click(closeButton);  // Click the "Add" button if present
    }

}
