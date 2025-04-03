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

    private final By fileUploadSection = By.id("file-upload-section");
    private final By termsConditionsDropdown = By.id("terms-conditions-dropdown");
    private final By submitButton = By.id("submit-button");
    private final By confirmationMessage = By.id("confirmation-message");

    private final NavigationHelper navigationHelper;

    public Direct_PO(WebDriver driver) {
        super(driver);
        this.navigationHelper = new NavigationHelper(driver);
    }

    private void performAction(String actionName, Runnable action, boolean isSubmit, String locator) {
        ErrorHandler.executeSafely(driver, action, actionName, isSubmit, locator);
    }

    public void navigateToDirectPO() {
        navigationHelper.navigateToModuleAndMenu(
                NavigationData.DIRECT_PO.getModuleName(),
                NavigationData.DIRECT_PO.getMenuName(),
                NavigationData.DIRECT_PO.getSubMenuName()
        );
    }

    public boolean isDirectPOPageLoaded() {
        return ErrorHandler.executeSafely(driver, () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            return wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//h1[contains(.,'Direct Purchase Order')]")
            )) != null;
        }, "Check if Direct PO Page is Loaded", false, "Direct PO Page Header");
    }

    public void clickDirectPOButton() {
        performAction("Click Direct PO Button", () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement directPOButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[contains(text(),'Direct PO')]")
            ));
            directPOButton.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(submitButton));
        }, false, "Direct PO Button");
    }

    public void selectTermsConditions(String terms) {
        performAction("Select Terms & Conditions", () -> {
            WebElement dropdown = findElement(termsConditionsDropdown);
            dropdown.click();
            WebElement option = driver.findElement(By.xpath("//option[contains(text(),'" + terms + "')]"));
            option.click();
        }, false, "Terms and Conditions");
    }

    public void clickSubmitButton() {
        performAction("Click Submit Button", () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            WebElement submitButtonElement = wait.until(ExpectedConditions.elementToBeClickable(submitButton));
            submitButtonElement.click();
        }, true, "Submit Button");
    }

    public void createDirectPO(String branch, String vendor, String currency, String quantity,
                               String price, String discount, String addOnCharges,
                               String additionalDiscount, String freightCharges,
                               String additionalTax, String roundOff,
                               String terms) {

        navigateToDirectPO();

        if (!isDirectPOPageLoaded()) {
            clickDirectPOButton();
        }

        fillPurchaseOrderDetails(branch, vendor, currency, quantity, price, discount, addOnCharges,
                additionalDiscount, freightCharges, additionalTax, roundOff);

        selectTermsConditions(terms);
        clickSubmitButton();
    }

    public String getConfirmationMessage() {
        return ErrorHandler.executeSafely(driver,
                () -> getText(confirmationMessage),
                "Get Confirmation Message",
                false,
                "Confirmation Message");
    }

    public String fetchPONumberFromConfirmation() {
        return ErrorHandler.executeSafely(driver, () -> {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.textMatches(confirmationMessage, Pattern.compile(".*(PO\\d+).*")));
            return getText(confirmationMessage).replaceAll(".*(PO\\d+).*", "$1");
        }, "Fetch PO Number", false, "confirmation-message");
    }
}