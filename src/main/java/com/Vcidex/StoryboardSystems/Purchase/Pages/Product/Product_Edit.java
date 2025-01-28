package com.Vcidex.StoryboardSystems.Purchase.Pages.Product;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Collections;
import java.util.List;

public class Product_Edit extends Product_Base_Page {

    // Constructor
    public Product_Edit(WebDriver driver) {
        super(driver);
    }

    // Specific Elements for Edit Page
    @FindBy(id = "updateButton")
    private WebElement updateButton;

    // Specific Method for Edit Page
    public void clickUpdateButton() {
        updateButton.click();
    }

    public void editProduct(String skuValue, String productName, String desc, String unit, String price, String type, List<String> taxes) {
        enterSKU(skuValue);
        enterProduct(productName);
        enterDescription(desc);
        selectUnit(unit);
        enterCostPrice(price);
        selectProductType(type);
        selectTaxes(Collections.singletonList(String.valueOf(taxes)));
        clickUpdateButton();
    }
}