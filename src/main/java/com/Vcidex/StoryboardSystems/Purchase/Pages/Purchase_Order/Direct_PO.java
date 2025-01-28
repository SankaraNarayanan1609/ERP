package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Direct_PO extends PurchaseBasePage {

    private static final String UPLOAD_FILE_LABEL = "Upload File";
    private static final String TERMS_CONDITIONS_LABEL = "Terms and Conditions";
    private static final String SAVE_AS_DRAFT_LABEL = "Save as Draft";
    private static final String SUBMIT_BUTTON_LABEL = "Submit";
    private static final String CONFIRMATION_MESSAGE_LABEL = "Confirmation Message";

    public Direct_PO(WebDriver driver) {
        super(driver);
    }

    public void uploadFile(String filePath) {
        enterTextUsingFollowingSibling(null, UPLOAD_FILE_LABEL, filePath);
    }

    public void selectTermsConditions(String terms) {
        selectDropdownUsingVisibleText(null, TERMS_CONDITIONS_LABEL, terms);
    }

    public void clickSaveAsDraft() {
        click(By.id(SAVE_AS_DRAFT_LABEL));
    }

    public void clickSubmitButton() {
        click(By.id(SUBMIT_BUTTON_LABEL));
    }

    public void createDirectPO(String filePath, String terms) {
        uploadFile(filePath);
        selectTermsConditions(terms);
        clickSubmitButton();
    }

    public String fetchCorrectPONumber(String vendorName, String branchName) {
        String rowsXPath = "//table[@id='poSummary']//tr";
        int rowCountBefore = getElementCount(By.xpath(rowsXPath));
        int rowCountAfter = getElementCount(By.xpath(rowsXPath));

        if (rowCountAfter > rowCountBefore) {
            int newRowIndex = rowCountAfter;
            String newRowXPath = "//table[@id='poSummary']//tr[" + newRowIndex + "]";

            if (isRowMatchingContext(newRowXPath, vendorName, branchName)) {
                String newPONumberXPath = newRowXPath + "//td[@data-column='poNumber']";
                return getText(By.xpath(newPONumberXPath));//Cannot resolve method 'getText' in 'Direct_PO'
            } else {
                throw new RuntimeException("Context mismatch: Vendor and Branch name do not match for the new row!");
            }
        } else {
            throw new RuntimeException("No new row found in the summary table after PO creation!");
        }
    }

    public boolean isRowMatchingContext(String newRowXPath, String vendorName, String branchName) {
        String vendorXPath = newRowXPath + "//td[@data-column='vendorName']";
        String branchXPath = newRowXPath + "//td[@data-column='branchName']";

        String vendorInRow = getText(By.xpath(vendorXPath));
        String branchInRow = getText(By.xpath(branchXPath));

        return vendorInRow.equalsIgnoreCase(vendorName) && branchInRow.equalsIgnoreCase(branchName);
    }

    public String getConfirmationMessage() {
        return getTextFromElementByLabel(CONFIRMATION_MESSAGE_LABEL);
    }

    public int getElementCount(By locator) {
        return driver.findElements(locator).size();
    }

    public String fetchPONumberFromConfirmation() {
        String confirmationMessage = getConfirmationMessage();
        return confirmationMessage.replaceAll(".*(PO\\d+).*", "$1");
    }
}
