package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;

public class PO_Summary extends BasePage {

    private static final String PO_TABLE_XPATH = "//table[@id='po-table']/tbody";
    private static final String PO_BAR_CHART_XPATH = "//div[@id='po-bar-chart']";

    private By emailButton = By.id("email-button");
    private By downloadButton = By.id("download-button");
    private By directPOButton = By.id("direct-po-button");
    private By raisePOButton = By.id("raise-po-button");
    private By invoiceTile = By.id("invoice-tile");
    private By orderTile = By.id("order-tile");

    public PO_Summary(WebDriver driver) {
        super(driver);
    }

    public void clickEmailButton() {
        waitForElement(emailButton).click();
    }

    public void clickDownloadButton() {
        waitForElement(downloadButton).click();
    }

    public void clickDirectPOButton() {
        waitForElement(directPOButton).click();
    }

    public void clickRaisePOButton() {
        waitForElement(raisePOButton).click();
    }

    public boolean clickEditButton(String poNumber) {
        WebElement editButton = findEditButtonForPO(poNumber);
        if (editButton != null) {
            waitForElement(editButton).click();
            return true;
        }
        System.err.println("❌ Edit button not found for PO Number: " + poNumber);
        return false;
    }

    public boolean clickViewButton(String poNumber) {
        WebElement viewButton = findViewButtonForPO(poNumber);
        if (viewButton != null) {
            waitForElement(viewButton).click();
            return true;
        }
        System.err.println("❌ View button not found for PO Number: " + poNumber);
        return false;
    }

    public String getInvoiceTileCount() {
        return getElementText(invoiceTile);
    }

    public String getOrderTileCount() {
        return getElementText(orderTile);
    }

    public List<WebElement> findElements(By locator) {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
    }

    private WebElement findEditButtonForPO(String poNumber) {
        List<WebElement> rows = findElements(By.xpath(PO_TABLE_XPATH + "/tr"));
        for (WebElement row : rows) {
            if (row.getText().contains(poNumber)) {
                return row.findElement(By.xpath("./td[last()]/button[contains(text(),'Edit')]"));
            }
        }
        return null;
    }

    private WebElement findViewButtonForPO(String poNumber) {
        List<WebElement> rows = findElements(By.xpath(PO_TABLE_XPATH + "/tr"));
        for (WebElement row : rows) {
            if (row.getText().contains(poNumber)) {
                return row.findElement(By.xpath("./td[last()]/button[contains(text(),'View')]"));
            }
        }
        return null;
    }

    public String getElementText(By locator) {
        WebElement element = waitForElement(locator);
        return element.getText();
    }

    private WebElement waitForElement(By locator) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement waitForElement(WebElement element) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }
}
