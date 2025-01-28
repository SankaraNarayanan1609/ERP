package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class PI_Add extends BasePage {

    // Locators for the elements on the PI Add page
    private By branchNameDropdown = By.id("branch-dropdown");  // Replace with the actual ID or XPath of the Branch dropdown
    private By refNoField = By.id("ref-no-field");  // Replace with the actual ID for Ref No field (Auto-generated, so read only)
    private By departmentField = By.id("department-field");  // Replace with the actual ID for department (Auto fetch)
    private By requestedByDropdown = By.id("requested-by-dropdown");  // Replace with the actual ID for Requested By dropdown
    private By priorityHighRadio = By.id("priority-high-radio");  // Replace with actual ID for High radio button
    private By priorityLowRadio = By.id("priority-low-radio");  // Replace with actual ID for Low radio button
    private By remarksField = By.id("remarks-field");  // Replace with actual ID for Remarks input
    private By productNameDropdown = By.id("product-name-dropdown");  // Replace with the actual ID for Product Name dropdown
    private By productGroupField = By.id("product-group-field");  // Replace with the actual ID for Product Group field (Auto fetched)
    private By productCodeField = By.id("product-code-field");  // Replace with the actual ID for Product Code field (Auto fetched)
    private By productDescriptionField = By.id("product-description-field");  // Replace with actual ID for Product Description
    private By unitField = By.id("unit-field");  // Replace with actual ID for Unit field (Auto fetched)
    private By quantityRequestedField = By.id("quantity-requested-field");  // Replace with actual ID for Quantity Requested field
    private By addButton = By.id("add-button");  // Replace with the actual ID for Add button

    // Constructor to initialize WebDriver
    public PI_Add(WebDriver driver) {
        super(driver);
    }

    // Method to select the Branch name from dropdown
    public void selectBranchName(String branchName) {
        selectDropdownOptionByVisibleText(branchNameDropdown, branchName);  // Select from dropdown
    }

    // Method to get the Ref No (auto-generated)
    public String getRefNo() {
        return getTextFromElementByLabel(String.valueOf(refNoField));  // Fetch the text of the Ref No field (if auto-generated, it should be visible)
    }

    // Method to fetch the Department (auto-fetch)
    public String getDepartment() {
        return getTextFromElementByLabel(String.valueOf(departmentField));  // Fetch department from field (if auto-fetched, it should be visible)
    }

    // Method to select the Requested By from dropdown
    public void selectRequestedBy(String requestedBy) {
        selectDropdownOptionByVisibleText(requestedByDropdown, requestedBy);  // Select from dropdown
    }

    // Method to select Priority (High/Low radio button)
    public void selectPriority(String priority) {
        if (priority.equalsIgnoreCase("high")) {
            click(priorityHighRadio);  // Click High priority radio button
        } else if (priority.equalsIgnoreCase("low")) {
            click(priorityLowRadio);  // Click Low priority radio button
        }
    }

    // Method to enter Remarks
    public void enterRemarks(String remarks) {
        sendKeys(remarksField, remarks);  // Type remarks into the input field
    }

    // Method to select the Product Name from dropdown and auto-fetch related details
    public void selectProductName(String productName) {
        selectDropdownOptionByVisibleText(productNameDropdown, productName);  // Select from dropdown
        // Call methods to fetch product details after selecting the product
        fetchProductDetails(productName);
    }

    // Method to fetch product details (auto-fill fields like Product Group, Product Code, etc.)
    private void fetchProductDetails(String productName) {
        // Simulate the auto-fetch logic
        // Ideally, the system will fetch details based on the selected product name.
        // You can retrieve these fields after selecting the product.
        // Example: Fetch Product Group, Product Code, Product Description, and Unit.
        String productGroup = getTextFromElementByLabel(String.valueOf(productGroupField));  // Auto-fetched
        String productCode = getTextFromElementByLabel(String.valueOf(productCodeField));  // Auto-fetched
        String productDescription = getTextFromElementByLabel(String.valueOf(productDescriptionField));  // Auto-fetched
        String unit = getTextFromElementByLabel(String.valueOf(unitField));  // Auto-fetched
    }

    // Method to enter the Quantity Requested
    public void enterQuantityRequested(String quantity) {
        sendKeys(quantityRequestedField, quantity);  // Enter the quantity requested
    }

    // Method to click on the Add button
    public void clickAddButton() {
        click(addButton);  // Click the "Add" button
    }

    // Helper method to select a dropdown option by visible text
    private void selectDropdownOptionByVisibleText(By locator, String option) {
        WebElement dropdown = findElement(locator);  // Find the dropdown element
        dropdown.click();
        List<WebElement> options = dropdown.findElements(By.tagName("option"));
        for (WebElement opt : options) {
            if (opt.getText().equals(option)) {
                opt.click();
                break;
            }
        }
    }

    // Helper method to get text from a field
    private String getTextFromElement(By locator) {
        WebElement element = findElement(locator);  // Find the element
        return element.getText();  // Return the text of the element
    }
}
