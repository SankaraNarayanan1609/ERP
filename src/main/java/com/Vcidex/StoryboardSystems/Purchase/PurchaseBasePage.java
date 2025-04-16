package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class PurchaseBasePage extends BasePage {

    public PurchaseBasePage(WebDriver driver) {
        super(driver);
    }

    public void selectBranchName(String branch) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Open the dropdown
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ng-select[@formcontrolname='branch_name']")));
        dropdown.click();

        // Type the branch name
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ng-select[@formcontrolname='branch_name']//input")));
        input.clear();
        input.sendKeys(branch);

        // Wait for the filtered option to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ng-dropdown-panel')]//span[contains(@class,'ng-option-label') and contains(text(),'" + branch + "')]")));

        // Press ENTER to select the highlighted option
        input.sendKeys(Keys.ENTER);
    }

    public void selectVendorName(String vendor) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Open the dropdown
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ng-select[@formcontrolname='vendor_companyname']")));
        dropdown.click();

        // Type the vendor name
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ng-select[@formcontrolname='vendor_companyname']//input")));
        input.clear();
        input.sendKeys(vendor);

        // Wait for any options to appear in the dropdown list (not matching vendor string directly)
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//div[contains(@class,'ng-dropdown-panel')]//div[contains(@class,'ng-option')]")));

        // Press ENTER to select the first filtered option
        input.sendKeys(Keys.ENTER);
    }

//    public void enterDeliveryTerms(String terms) {
//        sendKeys(getFollowingSiblingLocator(PurchaseConstants.DELIVERY_TERMS_LABEL), terms);
//    }
//
//    public void enterPaymentTerms(String terms) {
//        sendKeys(getFollowingSiblingLocator(PurchaseConstants.PAYMENT_TERMS_LABEL), terms);
//    }

    public void selectCurrency(String currency) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Open the dropdown
        WebElement dropdown = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//ng-select[@formcontrolname='currency']")));
        dropdown.click();

        // Type the currency name
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//ng-select[@formcontrolname='currency']//input")));
        input.clear();
        input.sendKeys(currency);

        // Wait for the filtered option to appear
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class,'ng-dropdown-panel')]//span[contains(@class,'ng-option-label') and contains(text(),'" + currency + "')]")));

        // Select with ENTER
        input.sendKeys(Keys.ENTER);
    }


    public void enterQuantity(String quantity) {
        WebElement qtyInput = driver.findElement(By.xpath("//input[@formcontrolname='productquantity']"));
        qtyInput.clear();
        qtyInput.sendKeys(quantity);
    }

    public void enterPrice(String unitprice) {
        WebElement qtyInput = driver.findElement(By.xpath("//input[@formcontrolname='unitprice']"));
        qtyInput.clear();
        qtyInput.sendKeys(unitprice);
    }

    public void enterDiscount(String productdiscount) {
        WebElement qtyInput = driver.findElement(By.xpath("//input[@formcontrolname='productdiscount']"));
        qtyInput.clear();
        qtyInput.sendKeys(productdiscount);
    }

    public void enterAddOnCharges(String addoncharge) {
        WebElement qtyInput = driver.findElement(By.xpath("//input[@formcontrolname='addoncharge']"));
        qtyInput.clear();
        qtyInput.sendKeys(addoncharge);
    }

    public void enterAdditionalDiscount(String additional_discount) {
        WebElement qtyInput = driver.findElement(By.xpath("//input[@formcontrolname='additional_discount']"));
        qtyInput.clear();
        qtyInput.sendKeys(additional_discount);
    }

    public void enterFreightCharges(String freightcharges) {
        WebElement qtyInput = driver.findElement(By.xpath("//input[@formcontrolname='freightcharges']"));
        qtyInput.clear();
        qtyInput.sendKeys(freightcharges);
    }

    public void selectAdditionalTax(String additional_tax) {
        WebElement qtyInput = driver.findElement(By.xpath("//input[@formcontrolname='tax_name4']"));
        qtyInput.clear();
        qtyInput.sendKeys(additional_tax);
    }

    public void enterRoundOff(String roundOff) {
        WebElement qtyInput = driver.findElement(By.xpath("//input[@formcontrolname='roundOff']"));
        qtyInput.clear();
        qtyInput.sendKeys(roundOff);
    }

//    public String getVendorDetails() {
//        return getText(getFollowingSiblingLocator(PurchaseConstants.VENDOR_DETAILS_LABEL));
//    }
//
//    public String getExchangeRate() {
//        return getText(getFollowingSiblingLocator(PurchaseConstants.EXCHANGE_RATE_LABEL));
//    }

//    public String getTotalAmount() {
//        return getText(getFollowingSiblingLocator(PurchaseConstants.TOTAL_AMOUNT_LABEL));
//    }
//
//    public String getGrandTotal() {
//        return getText(getFollowingSiblingLocator(PurchaseConstants.GRAND_TOTAL_LABEL));
//    }

    public void fillPurchaseOrderDetails(String branch, String vendor, String currency, String quantity,
                                         String price, String discount, String addOnCharges,
                                         String additionalDiscount, String freightCharges,
                                         String additionalTax, String roundOff) {
        if (branch != null && !branch.isEmpty()) {
            selectBranchName(branch);
        }
        if (vendor != null && !vendor.isEmpty()) {
            selectVendorName(vendor);
        }
        if (currency != null && !currency.isEmpty()) {
            selectCurrency(currency);
        }
        if (quantity != null && !quantity.isEmpty()) {
            enterQuantity(quantity);
        }
        if (price != null && !price.isEmpty()) {
            enterPrice(price);
        }
        if (discount != null && !discount.isEmpty()) {
            enterDiscount(discount);
        }
        if (addOnCharges != null && !addOnCharges.isEmpty()) {
            enterAddOnCharges(addOnCharges);
        }
        if (additionalDiscount != null && !additionalDiscount.isEmpty()) {
            enterAdditionalDiscount(additionalDiscount);
        }
        if (freightCharges != null && !freightCharges.isEmpty()) {
            enterFreightCharges(freightCharges);
        }
        if (additionalTax != null && !additionalTax.isEmpty()) {
            selectAdditionalTax(additionalTax);
        }
        if (roundOff != null && !roundOff.isEmpty()) {
            enterRoundOff(roundOff);
        }
    }
}