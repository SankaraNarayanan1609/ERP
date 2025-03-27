// Direct_PO.java
package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Database.DatabaseService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Direct_PO extends PurchaseBasePage {

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

    // ✅ Universal Action Wrapper
    private void performAction(String actionName, Runnable action, boolean isSubmit, String locator) {
        ErrorHandler.safeExecute(driver, action, actionName, isSubmit, locator);
    }

    public void uploadFile(String filePath) {
        performAction("Upload File", () -> sendKeys(getFollowingSiblingLocator(UPLOAD_FILE_LABEL), filePath), false, UPLOAD_FILE_LABEL);
    }

    public void selectTermsConditions(String terms) {
        performAction("Select Terms and Conditions",
                () -> selectDropdownUsingVisibleText(termsConditionsDropdown, terms),
                false,
                "terms-conditions-dropdown");
    }

    public void clickSaveAsDraft() {
        performAction("Click Save As Draft",
                () -> findElement(saveAsDraftButton).click(),
                false,
                "save-as-draft-button");
    }

    public void clickSubmitButton() {
        performAction("Click Submit Button",
                () -> findElement(submitButton).click(),
                true,
                "submit-button");
    }

    public void createDirectPO(String filePath, String terms) {
        performAction("Create Direct PO",
                () -> {
                    uploadFile(filePath);
                    selectTermsConditions(terms);
                    clickSubmitButton();
                    String apiResponse = getApiResponseForPO();
                    String poNumber = fetchPONumberFromConfirmation();

                    ErrorHandler.logInfo(driver, "API Response: " + apiResponse + ", PO Number: " + poNumber);
                },
                true,
                "create-direct-po");
    }

    public String getConfirmationMessage() {
        return ErrorHandler.safeExecute(driver,
                () -> getText(confirmationMessage),
                "Get Confirmation Message",
                false,
                "confirmation-message");
    }

    public String fetchPONumberFromConfirmation() {
        return ErrorHandler.safeExecute(driver, () -> {
            String confirmationMsg = getConfirmationMessage();

            int retries = 3;
            while (retries > 0) {
                if (confirmationMsg.matches(".*(PO\\d+).*")) {
                    return confirmationMsg.replaceAll(".*(PO\\d+).*", "$1");
                }
                retries--;
                waitForElement(confirmationMessage, 2);
            }

            String poFromDB = DatabaseService.fetchLatestPO();
            if (poFromDB != null) {
                return poFromDB;
            }

            throw new RuntimeException("No PO number found after retries & DB fallback!");
        }, "Fetch PO Number", false, "confirmation-message");
    }

    private String getApiResponseForPO() {
        return "{\"productType\": \"CONSUMABLE\"}";
    }
}