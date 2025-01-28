package com.Vcidex.StoryboardSystems.Purchase.Pages.Product;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import java.util.List;

public class Product_Base_Page {
    protected WebDriver driver;

    // Constructor
    public Product_Base_Page(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Common Elements
    @FindBy(id = "productGroupDropdown")
    protected WebElement productGroupDropdown;

    @FindBy(id = "productCode")
    protected WebElement productCode;  // Auto-generated, should not be editable

    @FindBy(id = "sku")
    protected WebElement sku;

    @FindBy(id = "product")
    protected WebElement product;

    @FindBy(id = "description")
    protected WebElement description;

    @FindBy(id = "unitDropdown")
    protected WebElement unitDropdown;

    @FindBy(id = "costPrice")
    protected WebElement costPrice;

    @FindBy(id = "productTypeDropdown")
    protected WebElement productTypeDropdown;

    @FindBy(id = "taxesDropdown")
    protected List<WebElement> taxesDropdown;  // Multi-select

    @FindBy(id = "backButton")
    protected WebElement backButton;

    // Common Methods
    public void selectProductGroup(String group) {
        // Code to select an item from productGroupDropdown
    }

    public String getProductCode() {
        return productCode.getText();  // Auto-generated, typically for view only
    }

    public void enterSKU(String skuValue) {
        sku.clear();
        sku.sendKeys(skuValue);
    }

    public void enterProduct(String productName) {
        product.clear();
        product.sendKeys(productName);
    }

    public void enterDescription(String desc) {
        description.clear();
        description.sendKeys(desc);
    }

    public void selectUnit(String unit) {
        // Code to select an item from unitDropdown
    }

    public void enterCostPrice(String price) {
        costPrice.clear();
        costPrice.sendKeys(price);
    }

    public void selectProductType(String type) {
        // Code to select an item from productTypeDropdown
    }

    public void selectTaxes(List<String> taxes) {
        // Code to select multiple items from taxesDropdown
    }

    public void clickBackButton() {
        backButton.click();
    }
}