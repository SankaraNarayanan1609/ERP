package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PO_Edit extends PurchaseBasePage {

    // Constructor
    public PO_Edit(WebDriver driver) {
        super(driver);
    }

    // Unique Elements for Direct_PO_Edit
    @FindBy(id = "UploadFile")
    private WebElement uploadFile;

    @FindBy(id = "ViewUploadedFile")
    private WebElement viewUploadedFile;

    @FindBy(id = "productSummaryTable")
    private WebElement productSummaryTable;

    @FindBy(xpath = "//button[contains(text(), 'Delete')]")
    private WebElement productDeleteButton;

    @FindBy(id = "termsConditionsDropdown")
    private WebElement termsConditionsDropdown;

    @FindBy(id = "updateButton")
    private WebElement updateButton;

    // Methods for Direct_PO_Edit
    public void uploadFile(String filePath) {
        uploadFile.sendKeys(filePath);
    }

    public String getUploadedFileName() {
        return viewUploadedFile.getText();
    }

    public void deleteProductFromSummary() {
        productDeleteButton.click();
    }

    public void selectTermsConditions(String terms) {
        termsConditionsDropdown.sendKeys(terms);
    }

    public void clickUpdateButton() {
        updateButton.click();
    }
}
