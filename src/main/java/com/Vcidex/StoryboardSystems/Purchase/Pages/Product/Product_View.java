package com.Vcidex.StoryboardSystems.Purchase.Pages.Product;

import org.openqa.selenium.WebDriver;

public class Product_View extends Product_Base_Page {

    // Constructor
    public Product_View(WebDriver driver) {
        super(driver);
    }

    // Methods specific to view product details
    public String viewSKU() {
        return sku.getAttribute("value");
    }

    public String viewProduct() {
        return product.getAttribute("value");
    }

    public String viewDescription() {
        return description.getAttribute("value");
    }

    public String viewCostPrice() {
        return costPrice.getAttribute("value");
    }

    // Additional methods for viewing specific details, if needed
}