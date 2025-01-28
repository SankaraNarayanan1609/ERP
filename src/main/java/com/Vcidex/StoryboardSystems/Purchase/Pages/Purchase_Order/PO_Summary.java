package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class PO_Summary extends BasePage {

    // Locators for PO_Summary page elements
    private By emailButton = By.id("email-button");  // Email Button
    private By downloadButton = By.id("download-button");  // Download Button
    private By directPOButton = By.id("direct-po-button");  // Direct PO Button
    private By raisePOButton = By.id("raise-po-button");  // Raise PO Button
    private By editButton = By.xpath("//table[@id='po-table']/tbody/tr/td[last()]/button[contains(text(),'Edit')]");  // Edit Button in the table
    private By viewButton = By.xpath("//table[@id='po-table']/tbody/tr/td[last()]/button[contains(text(),'View')]");  // View Button in the table
    private By barChart = By.id("po-bar-chart");  // Bar Chart ID
    private By invoiceTile = By.id("invoice-tile");  // Invoice tile with count
    private By orderTile = By.id("order-tile");  // Order tile with count

    // Constructor to initialize WebDriver
    public PO_Summary(WebDriver driver) {
        super(driver);
    }

    // Method to click the "Email" button
    public void clickEmailButton() {
        click(emailButton);  // Click the email button
    }

    // Method to click the "Download" button
    public void clickDownloadButton() {
        click(downloadButton);  // Click the download button
    }

    // Method to click the "Direct PO" button
    public void clickDirectPOButton() {
        click(directPOButton);  // Click the Direct PO button
    }

    // Method to click the "Raise PO" button
    public void clickRaisePOButton() {
        click(raisePOButton);  // Click the Raise PO button
    }

    // Method to click the "Edit" button for a specific PO row by index
    public void clickEditButton(int rowIndex) {
        WebElement row = findElement(By.xpath("//table[@id='po-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'Edit')]"));
        click((By) row);  // Click the Edit button in the specified row
    }

    // Method to click the "View" button for a specific PO row by index
    public void clickViewButton(int rowIndex) {
        WebElement row = findElement(By.xpath("//table[@id='po-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'View')]"));
        click((By) row);  // Click the View button in the specified row
    }

    // Method to get the bar chart element (Optional, for verification or interaction)
    public WebElement getBarChart() {
        return findElement(barChart);  // Return the bar chart WebElement
    }

    // Method to get the Invoice tile (contains the count)
    public String getInvoiceTileCount() {
        return getElementText(invoiceTile);  // Return the text (count) of the Invoice tile
    }

    // Method to get the Order tile (contains the count)
    public String getOrderTileCount() {
        return getElementText(orderTile);  // Return the text (count) of the Order tile
    }

    // Method to get the value of the bar chart (optional for dynamic interaction)
    public String getBarChartOrderValue(int monthIndex) {
        // Assuming the bar chart displays the order value for the months (use bar chart element's index)
        WebElement bar = findElement(By.xpath("//div[@id='po-bar-chart']/div[" + monthIndex + "]"));
        return bar.getAttribute("title");  // Assuming the value is displayed in a tooltip/title attribute
    }

    // Optional: Method to get the count of rows in the PO table
    public int getTableRowCount() {
        List<WebElement> rows = findElements(By.xpath("//table[@id='po-table']/tbody/tr"));  // Get all rows in the PO table
        return rows.size();
    }

}
