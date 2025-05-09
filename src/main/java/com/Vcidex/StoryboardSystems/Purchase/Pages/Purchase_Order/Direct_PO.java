package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Direct_PO extends PurchaseBasePage {

    public Direct_PO(WebDriver driver) {
        super(driver);
    }

    // ------------------- Unique Locators -------------------

    private final By branchNameDropdown = By.xpath("//ng-select[@formcontrolname='branchName']");
    private final By poRefNoInput = By.xpath("//input[@formcontrolname='PO Ref.No']");
    private final By poDateInput = By.xpath("//input[@formcontrolname='po_date']");
    private final By expectedDateInput = By.xpath("//input[@formcontrolname='expected_date']");
    private final By vendorNameDropdown = By.xpath("//ng-select[@formcontrolname='vendorName']");
    private final By vendorDetailsInput = By.xpath("//ng-select[@formcontrolname='vendorDetails']");
    private final By billToInput = By.xpath("//input[@formcontrolname='address1']");
    private final By shipToInput = By.xpath("//input[@formcontrolname='address2']");
    private final By requestedByDropdown = By.xpath("//ng-select[@formcontrolname='employee_name']");
    private final By requestorContactInput = By.xpath("//input[@formcontrolname='Requestor_details']");
    private final By deliveryTermsInput = By.xpath("//input[@formcontrolname='delivery_terms']");
    private final By paymentTermsInput = By.xpath("//input[@formcontrolname='payment_terms']");
    private final By despatchModeInput = By.xpath("//input[@formcontrolname='despatch_mode']");
    private final By currencyDropdown = By.xpath("//ng-select[@formcontrolname='currency_code']");
    private final By exchangeRateInput = By.xpath("//input[@formcontrolname='exchange_rate']");
    private final By coverNoteInput = By.xpath("//textarea[@formcontrolname='po_covernote']");
    private final By renewalYesRadio = By.xpath("//label[contains(., 'Yes')]/preceding-sibling::input[@formcontrolname='renewal_mode']");
    private final By renewalNoRadio = By.xpath("//label[contains(., 'No')]/preceding-sibling::input[@formcontrolname='renewal_mode']");
    private final By renewalDateInput = By.xpath("//input[@formcontrolname='renewal_date']");
    private final By frequencyDropdown = By.xpath("//ng-select[@formcontrolname='frequency_terms']");
    private final By productAddButton = By.xpath("//button[contains(text(), 'Add')]");
    private final By productDeleteButton = By.xpath("//button[contains(text(), 'Delete')]");
    private final By termsDropdown = By.xpath("//ng-select[@formcontrolname='po_terms_conditions']");
    private final By saveAsDraftButton = By.xpath("//button[contains(text(), 'Save as Draft')]");
    private final By submitButton = By.xpath("//button[contains(text(), 'Submit')]");
    private final By cancelButton = By.xpath("//button[contains(text(), 'Cancel')]");
    private final By draftHistoryButton = By.xpath("//button[contains(text(), 'Draft History')]");

    // ------------------- Action Methods -------------------

    public void fillPOForm(PurchaseOrderData poData) {
        if (poData.getBranchName() != null) {
            sendKeys(branchNameDropdown, poData.getBranchName());
        }
        if (poData.getPoRefNo() != null) {
            sendKeys(poRefNoInput, poData.getPoRefNo());
        }
        if (poData.getPoDate() != null) {
            sendKeys(poDateInput, poData.getPoDate().toString());
        }
        if (poData.getExpectedDate() != null) {
            sendKeys(expectedDateInput, poData.getExpectedDate().toString());
        }
        if (poData.getExpectedDate() != null) {
            sendKeys(vendorNameDropdown, poData.getVendorName().toString());
        }
        if (poData.getExpectedDate() != null) {
            sendKeys(vendorDetailsInput, poData.getVendorDetails().toString());
        }
        if (poData.getBillTo() != null) {
            sendKeys(billToInput, poData.getBillTo());
        }
        if (poData.getShipTo() != null) {
            sendKeys(shipToInput, poData.getShipTo());
        }
        if (poData.getRequestedBy() != null) {
            selectDropdownUsingVisibleText(requestedByDropdown, poData.getRequestedBy());
        }
        if (poData.getRequestorContactDetails() != null) {
            sendKeys(requestorContactInput, poData.getRequestorContactDetails());
        }
        if (poData.getDeliveryTerms() != null) {
            sendKeys(deliveryTermsInput, poData.getDeliveryTerms());
        }
        if (poData.getPaymentTerms() != null) {
            sendKeys(paymentTermsInput, poData.getPaymentTerms());
        }
        if (poData.getDespatchMode() != null) {
            sendKeys(despatchModeInput, poData.getDespatchMode());
        }
        if (poData.getCurrency() != null) {
            selectDropdownUsingVisibleText(currencyDropdown, poData.getCurrency());
        }
        if (poData.getExchangeRate() != null) {
            sendKeys(exchangeRateInput, String.valueOf(poData.getExchangeRate()));
        }
        if (poData.getCoverNote() != null) {
            sendKeys(coverNoteInput, poData.getCoverNote());
        }
        if (poData.getRenewalDate() != null) {
            sendKeys(renewalDateInput, poData.getRenewalDate().toString());
        }
        if (poData.getFrequency() != null) {
            selectDropdownUsingVisibleText(frequencyDropdown, poData.getFrequency());
        }
    }

    public void clickSubmit() {
        click(submitButton);
    }

    public void clickSaveAsDraft() {
        click(saveAsDraftButton);
    }


    public boolean isPOSubmitted() {
        By submittedStatus = By.xpath("//span[contains(text(),'Submitted') or contains(@class,'submitted-status')]");
        return isElementPresent(submittedStatus);
    }

    public boolean isPOCancelled() {
        By cancelledStatus = By.xpath("//span[contains(text(),'Cancelled') or contains(@class,'cancelled-status')]");
        return isElementPresent(cancelledStatus);
    }

    public void clickAddProduct() {
        click(productAddButton);
    }

    public void clickDeleteProduct() {
        click(productDeleteButton);
    }

    public void clickCancel() {
        click(cancelButton);
    }

    public void clickDraftHistory() {
        click(draftHistoryButton);
    }
}