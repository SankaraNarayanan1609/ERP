package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;


public class Direct_PO extends PurchaseBasePage {
    private static final Logger logger = LogManager.getLogger(Direct_PO.class);

    private static final String UPLOAD_FILE_LABEL = "Upload File";
    private static final String TERMS_CONDITIONS_LABEL = "Terms and Conditions";
    private static final String CONFIRMATION_MESSAGE_LABEL = "Confirmation Message";

    private final By fileUploadSection = By.id("file-upload-section");
    private final By termsConditionsDropdown = By.id("terms-conditions-dropdown");
    private final By saveAsDraftButton = By.id("save-as-draft-button");
    private final By submitButton = By.id("submit-button");
    private final By confirmationMessage = By.id("confirmation-message");

    public Direct_PO(WebDriver driver) {
        super(driver);
    }

    public void uploadFile(String filePath) {
        enterText(getFollowingSiblingLocator(UPLOAD_FILE_LABEL), filePath);
    }

    public void selectTermsConditions(String terms) {
        selectDropdownUsingVisibleText(termsConditionsDropdown, terms);
    }

    public void clickSaveAsDraft() {
        findElement(saveAsDraftButton).click();
        logger.info("✅ Clicked 'Save as Draft' button");
    }

    public void clickSubmitButton() {
        findElement(submitButton).click();
        logger.info("✅ Clicked 'Submit' button");
    }

    public void createDirectPO(String filePath, String terms) {
        uploadFile(filePath);
        selectTermsConditions(terms);
        clickSubmitButton();
    }

    public String fetchCorrectPONumber(String vendorName, String branchName) {
        String rowsXPath = "//table[@id='poSummary']//tr";

        int timeout = Integer.parseInt(ConfigManager.getProperty("WebDriver.timeout", "10"));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.xpath(rowsXPath), 1));

        String newRowXPath = "//table[@id='poSummary']//tr[last()]";
        if (isRowMatchingContext(newRowXPath, vendorName, branchName)) {
            By poNumberLocator = By.xpath(String.format("%s//td[@data-column='poNumber']", newRowXPath));
            return getText(poNumberLocator);
        }

        logger.error("❌ No matching row found for Vendor: {}, Branch: {}", vendorName, branchName);
        captureScreenshot("No_Matching_PO_" + System.currentTimeMillis());
        throw new RuntimeException("No matching row found!");
    }

    public boolean isRowMatchingContext(String newRowXPath, String vendorName, String branchName) {
        By vendorXPath = By.xpath(String.format("%s//td[@data-column='vendorName']", newRowXPath));
        By branchXPath = By.xpath(String.format("%s//td[@data-column='branchName']", newRowXPath));

        String vendorInRow = getText(vendorXPath);
        String branchInRow = getText(branchXPath);

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

        logger.error("❌ No PO number found in confirmation message!");
        captureScreenshot("No_PO_Found_" + System.currentTimeMillis());
        throw new RuntimeException("No PO number found in confirmation message!");
    }
}
