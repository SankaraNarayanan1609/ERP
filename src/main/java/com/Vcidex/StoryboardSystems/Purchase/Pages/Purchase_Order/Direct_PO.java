package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.Vcidex.StoryboardSystems.Utils.Reporting.TestLogger;
import com.Vcidex.StoryboardSystems.Utils.Database.DatabaseService;

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

        String apiResponse = getApiResponseForPO();
        TestLogger.captureApiResponse(apiResponse, fetchPONumberFromConfirmation());
    }

    public String getConfirmationMessage() {
        return getText(confirmationMessage);
    }

    public String fetchPONumberFromConfirmation() {
        String confirmationMsg = getConfirmationMessage();

        int retries = 3;
        while (retries > 0) {
            if (confirmationMsg.matches(".*(PO\\d+).*")) {
                return confirmationMsg.replaceAll(".*(PO\\d+).*", "$1");
            }
            logger.warn("⚠️ PO number not found, retrying... Attempts left: " + retries);
            retries--;
            waitForElement(confirmationMessage, 2); // ✅ Fixed: Passing `By.id()`
        }

        logger.error("❌ No PO number found in confirmation message! Fallback needed.");

        String poFromDB = DatabaseService.fetchLatestPO(); // ✅ Fixed: Implemented method
        if (poFromDB != null) {
            return poFromDB;
        }

        captureScreenshot("No_PO_Found_" + System.currentTimeMillis());
        throw new RuntimeException("No PO number found after retries & DB fallback!");
    }

    private String getApiResponseForPO() {
        return "{\"productType\": \"CONSUMABLE\"}";
    }
}
