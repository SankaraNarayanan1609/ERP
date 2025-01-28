package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;


import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class Raise_PO extends PurchaseBasePage {

    // Constructor
    public Raise_PO(WebDriver driver) {
        super(driver);
    }

    // Unique Elements for Raise_PO
    @FindBy(id = "UploadFile")
    private WebElement uploadFile;

    @FindBy(css = "input[type='checkbox'][name='ProductCheckbox']")
    private WebElement productCheckbox;

    @FindBy(id = "CheckAll")
    private WebElement checkAll;

    @FindBy(id = "productSummaryTable")
    private WebElement productSummaryTable;

    @FindBy(xpath = "//button[contains(text(), 'Delete')]")
    private WebElement productDeleteButton;

    @FindBy(id = "termsConditionsDropdown")
    private WebElement termsConditionsDropdown;

    @FindBy(id = "SubmitButton")
    private WebElement submitButton;

    // Methods for Raise_PO
    public void uploadFile(String filePath) {
        uploadFile.sendKeys(filePath);
    }

    public void selectProductCheckbox() {
        productCheckbox.click();
    }

    public void selectCheckAll() {
        checkAll.click();
    }

    public void deleteProductFromSummary() {
        productDeleteButton.click();
    }

    public void selectTermsConditions(String terms) {
        termsConditionsDropdown.sendKeys(terms);
    }

    public void clickSubmitButton() {
        submitButton.click();
    }
}

