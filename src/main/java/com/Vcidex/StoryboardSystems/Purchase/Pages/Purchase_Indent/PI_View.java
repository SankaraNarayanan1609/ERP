package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PI_View extends BasePage {

    // Locators for PI_View page
    private By refNoField = By.id("refNo");
    private By departmentField = By.id("department");
    private By requestedByField = By.id("requestedBy");
    private By priorityField = By.id("priority");
    private By remarksField = By.id("remarks");
    private By productNameField = By.id("productName");
    private By productGroupField = By.id("productGroup");
    private By productCodeField = By.id("productCode");
    private By productDescriptionField = By.id("productDescription");
    private By unitField = By.id("unit");
    private By quantityRequestedField = By.id("quantityRequested");
    private By closeButton = By.id("closeButton");  // Fixed the correct locator name

    // Constructor
    public PI_View(WebDriver driver) {
        super(driver);
    }

    // ✅ Generic method to get text using locator
    private String getFieldText(By locator) {
        return getText(locator);
    }

    // ✅ Using the generic method for all fields
    public String getRefNo() {
        return getFieldText(refNoField);
    }

    public String getDepartment() {
        return getFieldText(departmentField);
    }

    public String getRequestedBy() {
        return getFieldText(requestedByField);
    }

    public String getPriority() {
        return getFieldText(priorityField);
    }

    public String getRemarks() {
        return getFieldText(remarksField);
    }

    public String getProductName() {
        return getFieldText(productNameField);
    }

    public String getProductGroup() {
        return getFieldText(productGroupField);
    }

    public String getProductCode() {
        return getFieldText(productCodeField);
    }

    public String getProductDescription() {
        return getFieldText(productDescriptionField);
    }

    public String getUnit() {
        return getFieldText(unitField);
    }

    public String getQuantityRequested() {
        return getFieldText(quantityRequestedField);
    }

    // ✅ Click the close button (if applicable)
    public void clickCloseButton() {
        click(closeButton);
    }
}