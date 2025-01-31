package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Direct_PO extends PurchaseBasePage {

    private static final String UPLOAD_FILE_LABEL = "Upload File";
    private static final String TERMS_CONDITIONS_LABEL = "Terms and Conditions";
    private static final String CONFIRMATION_MESSAGE_LABEL = "Confirmation Message";

    private By fileUploadSection = By.id("file-upload-section");
    private By termsConditionsDropdown = By.id("terms-conditions-dropdown");
    private By saveAsDraftButton = By.id("save-as-draft-button"); // Replace with actual ID
    private By submitButton = By.id("submit-button"); // Replace with actual ID
    private By confirmationMessage = By.id("confirmation-message"); // Replace with actual ID

    public Direct_PO(WebDriver driver) {
        super(driver);
    }

    public void uploadFile(String filePath) {
        enterTextUsingFollowingSibling(getFollowingSiblingLocator(UPLOAD_FILE_LABEL), filePath);
    }


    public void selectTermsConditions(String terms) {
        selectDropdownUsingVisibleText(termsConditionsDropdown, terms);
    }

    public void clickSaveAsDraft() {
        click((WebElement) saveAsDraftButton, true); // ✅ Handles unexpected alerts before clicking
    }

    public void clickSubmitButton() {
        click((WebElement) submitButton, false); // ✅ Does NOT handle alerts before clicking
    }

    public void createDirectPO(String filePath, String terms) {
        uploadFile(filePath);
        selectTermsConditions(terms);
        clickSubmitButton();
    }

    public String fetchCorrectPONumber(String vendorName, String branchName) {
        String rowsXPath = "//table[@id='poSummary']//tr";

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(rowsXPath), 1));

        String newRowXPath = "//table[@id='poSummary']//tr[last()]";

        if (isRowMatchingContext(newRowXPath, vendorName, branchName)) {
            return getText(By.xpath(newRowXPath + "//td[@data-column='poNumber']"));
        }

        throw new RuntimeException("No matching row found!");
    }

    public boolean isRowMatchingContext(String newRowXPath, String vendorName, String branchName) {
        String vendorXPath = newRowXPath + "//td[@data-column='vendorName']";
        String branchXPath = newRowXPath + "//td[@data-column='branchName']";

        String vendorInRow = getText(By.xpath(vendorXPath));
        String branchInRow = getText(By.xpath(branchXPath));

        return vendorInRow.equalsIgnoreCase(vendorName) && branchInRow.equalsIgnoreCase(branchName);
    }

    public String getConfirmationMessage() {
        return getText(confirmationMessage);
    }

    public String fetchPONumberFromConfirmation() {
        String confirmationMessage = getConfirmationMessage();
        if (confirmationMessage.matches(".*(PO\\d+).*")) {
            return confirmationMessage.replaceAll(".*(PO\\d+).*", "$1");
        }
        throw new RuntimeException("No PO number found in confirmation message!");
    }
}
