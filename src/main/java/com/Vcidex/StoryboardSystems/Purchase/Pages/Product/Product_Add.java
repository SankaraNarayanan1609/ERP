package com.Vcidex.StoryboardSystems.Purchase.Pages.Product;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Collections;
import java.util.List;

public class Product_Add extends Product_Base_Page {

    // Constructor
    public Product_Add(WebDriver driver) {
        super(driver);
    }

    // Specific Elements for Add Page
    @FindBy(id = "submitButton")
    private WebElement submitButton;

    // Specific Method for Add Page
    public void clickSubmitButton() {
        submitButton.click();
    }

    public void addProduct(String skuValue, String productName, String desc, String unit, String price, String type, List<String> taxes) {
        enterSKU(skuValue);
        enterProduct(productName);
        enterDescription(desc);
        selectUnit(unit);
        enterCostPrice(price);
        selectProductType(type);
        selectTaxes(Collections.singletonList(String.valueOf(taxes)));
        clickSubmitButton();
    }
}