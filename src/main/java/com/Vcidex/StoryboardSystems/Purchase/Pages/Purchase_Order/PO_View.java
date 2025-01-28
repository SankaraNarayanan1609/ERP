package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PO_View extends PurchaseBasePage {

    // Constructor
    public PO_View(WebDriver driver) {
        super(driver);
    }

    // Unique Elements for Direct_PO_View
    @FindBy(id = "ViewUploadedFile")
    private WebElement viewUploadedFile;

    // Methods for Direct_PO_View
    public String getUploadedFileName() {
        return viewUploadedFile.getText();
    }

    public boolean isUploadedFileVisible() {
        return viewUploadedFile.isDisplayed();
    }
}

