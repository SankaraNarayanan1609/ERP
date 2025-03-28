package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationData;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.regex.Pattern;

import java.time.Duration;

public class Direct_PO extends PurchaseBasePage {

    private static final String UPLOAD_FILE_LABEL = "Upload File";
    private static final String TERMS_CONDITIONS_LABEL = "Terms and Conditions";
    private static final String CONFIRMATION_MESSAGE_LABEL = "Confirmation Message";

    private final By fileUploadSection = By.id("file-upload-section");
    private final By termsConditionsDropdown = By.id("terms-conditions-dropdown");
    private final By saveAsDraftButton = By.id("save-as-draft-button");
    private final By submitButton = By.id("submit-button");
    private final By confirmationMessage = By.id("confirmation-message");

    private NavigationHelper navigationHelper;

    public Direct_PO(WebDriver driver) {
        super(driver);
        this.navigationHelper = new NavigationHelper(driver);
    }

    // ✅ Navigate to Direct PO using NavigationHelper
    public void navigateToDirectPO() {
        navigationHelper.navigateToModuleAndMenu(
                NavigationData.DIRECT_PO.getModuleName(),
                NavigationData.DIRECT_PO.getMenuName(),
                NavigationData.DIRECT_PO.getSubMenuName()
        );
    }

    // ✅ Universal Action Wrapper
    private void performAction(String actionName, Runnable action, boolean isSubmit, String locator) {
        ErrorHandler.safeExecute(driver, action, actionName, isSubmit, locator);
    }

    public void uploadFile(String filePath) {
        performAction("Upload File", () -> sendKeys(fileUploadSection, filePath), false, UPLOAD_FILE_LABEL);
    }

    public void selectTermsConditions(String terms) {
        performAction("Select Terms and Conditions",
                () -> selectDropdownUsingVisibleText(termsConditionsDropdown, terms),
                false,
                "terms-conditions-dropdown");
    }

//    public void clickSaveAsDraft() {
//        performAction("Click Save As Draft",
//                () -> findElement(saveAsDraftButton).click(),
//                false,
//                "save-as-draft-button");
//    }

    public void clickSubmitButton() {
        performAction("Click Submit Button",
                () -> findElement(submitButton).click(),
                true,
                "submit-button");
    }

    public void createDirectPO(String filePath, String terms) {
        navigateToDirectPO(); // Ensure correct navigation before performing actions
        performAction("Create Direct PO",
                () -> {
//                    uploadFile(filePath);
//                    selectTermsConditions(terms);
                    clickSubmitButton();
                    String apiResponse = getApiResponseForPO();
                    String poNumber = fetchPONumberFromConfirmation();

                    ErrorHandler.logInfo(driver, "API Response: " + apiResponse + ", PO Number: " + poNumber);//Cannot resolve method 'logInfo' in 'ErrorHandler'
                },
                true,
                "create-direct-po");
    }

    public String getConfirmationMessage() {
        return ErrorHandler.safeExecute(driver,//Cannot resolve method 'safeExecute' in 'ErrorHandler'
                () -> getText(confirmationMessage),
                "Get Confirmation Message",
                false,
                "confirmation-message");
    }

    public String fetchPONumberFromConfirmation() {
        return ErrorHandler.safeExecute(driver, () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.textMatches(confirmationMessage, Pattern.compile(".*(PO\\d+).*")));

            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationMessage));
            String confirmationMsg = getText(confirmationMessage);

            return confirmationMsg.replaceAll(".*(PO\\d+).*", "$1");
        }, "Fetch PO Number", false, "confirmation-message");
    }

    private String getApiResponseForPO() {
        return "{\"productType\": \"CONSUMABLE\"}";
    }
}