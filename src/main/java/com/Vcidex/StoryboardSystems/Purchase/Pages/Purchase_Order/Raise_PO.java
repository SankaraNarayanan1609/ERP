package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Purchase.Business.DirectPurchaseOrderLogger;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Raise_PO extends PurchaseBasePage {
    private final By uploadFileInput         = By.id("UploadFile");
    private final By productCheckbox         = By.cssSelector("input[type='checkbox'][name='ProductCheckbox']");
    private final By checkAllCheckbox        = By.id("CheckAll");
    private final By productDeleteButton     = By.xpath("//button[contains(type(), 'Delete')]");
    private final By termsConditionsDropdown = By.id("termsConditionsDropdown");
    private final By submitButton            = By.id("SubmitButton");
    private final By poNumberLocator         = By.cssSelector(".po-number");  // adjust as needed

    public Raise_PO(WebDriver driver) {
        super(driver);
    }

    /**
     * High-level flow: upload, select products, set terms & submit.
     * Returns the newly created PO number.
     */
    public String raiseAndSubmitPO(PurchaseOrderData data) {
        // 1) start the PO flow
        DirectPurchaseOrderLogger.startPOCreation(data.getVendorName());

        // 2) upload any file (if provided)
        if (data.getUploadFilePath() != null) {
            type(uploadFileInput, data.getUploadFilePath());
            DirectPurchaseOrderLogger.addedGeneralDetails("UploadFile", data.getUploadFilePath());
        }

        // 3) choose products
        if (data.isSelectAllProducts()) {
            click(checkAllCheckbox, "Select All Products");
            DirectPurchaseOrderLogger.addedGeneralDetails("ProductSelection", "All");
        } else {
            click(productCheckbox, "Select Single Product");
            DirectPurchaseOrderLogger.addedProduct("Custom selection", 1);
        }

        // 4) remove any if needed
        if (data.isDeleteAfterSelect()) {
            click(productDeleteButton, "Delete Selected Product");
            DirectPurchaseOrderLogger.addedProduct("Deleted selection", 1);
        }

        // 5) set terms & conditions
        if (data.getTermsConditions() != null) {
            selectByText(termsConditionsDropdown, data.getTermsConditions());
            DirectPurchaseOrderLogger.addedGeneralDetails("TermsConditions", data.getTermsConditions());
        }

        // 6) submit
        DirectPurchaseOrderLogger.submittingPO();
        click(submitButton, "Submit Raised PO");

        // 7) read back the PO number & log success
        String poNumber = getText(poNumberLocator, "PO Number");
        DirectPurchaseOrderLogger.poCreationSuccess(poNumber, data.getVendorName());

        return poNumber;
    }
}