package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import com.Vcidex.StoryboardSystems.Utils.Reporting.ErrorHandler;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationData;
import com.Vcidex.StoryboardSystems.Utils.Navigation.NavigationHelper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.regex.Pattern;

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
        ErrorHandler.executeSafely(driver, action, actionName, isSubmit, locator);
    }

    public void clickDirectPOButton() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));  // Increased wait time

            // Wait for the 'Direct PO' button to be clickable
            WebElement directPOButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(),'Direct PO')]")));
            directPOButton.click();
            System.out.println("Button with text 'Direct PO' clicked.");

            // Wait until the Direct PO page has fully loaded (check for submit button)
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("submit-button")));
            System.out.println("Direct PO page loaded, Submit button is visible.");
        } catch (TimeoutException e) {
            System.out.println("Button visibility timeout: " + e.getMessage());
        }
    }

    public boolean isDirectPOPageLoaded() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h1[contains(.,'Direct Purchase Order')]")));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void uploadFile(String filePath) {
        performAction("Upload File", () -> {
            WebElement uploadElement = findElement(fileUploadSection);
            uploadElement.sendKeys(filePath); // Standard file upload
        }, false, UPLOAD_FILE_LABEL);
    }

    public void clickSubmitButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        try {
            // Wait for the Submit button to be clickable
            WebElement submitButtonElement = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
            submitButtonElement.click();
            System.out.println("Submit button clicked.");
        } catch (TimeoutException e) {
            System.out.println("Submit button not clickable: " + e.getMessage());
        }
    }

    public void createDirectPO(String filePath, String terms) {
        navigateToDirectPO();  // Navigate to Purchase Order page

        if (!isDirectPOPageLoaded()) {
            System.out.println("Direct PO page NOT detected. Clicking Direct PO button...");
            clickDirectPOButton();  // Click only if the page is not already open
        } else {
            System.out.println("Already on Direct PO page. Skipping button click");
        }

        performAction("Create Direct PO", () -> {
            uploadFile(filePath);
            clickSubmitButton();
        }, true, "create-direct-po"); // Pass "create-direct-po" to log this action in the report
    }

//    public String getConfirmationMessage() {
//        return ErrorHandler.executeSafely(driver,
//                () -> getText(confirmationMessage),
//                "Get Confirmation Message",
//                false,
//                "confirmation-message");
//    }

//    public String fetchPONumberFromConfirmation() {
//        return ErrorHandler.executeSafely(driver, () -> {
//            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
//            wait.until(ExpectedConditions.textMatches(confirmationMessage, Pattern.compile(".*(PO\\d+).*")));
//
//            wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationMessage));
//            String confirmationMsg = getText(confirmationMessage);
//
//            return confirmationMsg.replaceAll(".*(PO\\d+).*", "$1");
//        }, "Fetch PO Number", false, "confirmation-message");
//    }
//
//    private String getApiResponseForPO() {
//        return "{\"productType\"}";
//    }
}