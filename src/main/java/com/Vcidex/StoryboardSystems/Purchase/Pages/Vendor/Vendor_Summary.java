package com.Vcidex.StoryboardSystems.Purchase.Pages.Vendor;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class Vendor_Summary extends BasePage {

    // Locators for Vendor Summary page elements
    private By searchBox = By.id("vendor-search-bar");  // Replace with actual search bar locator for Vendor
    private By showEntriesSelector = By.id("vendor-show-entries-selector");  // Replace with actual selector for Vendor
    private By addButton = By.id("vendor-add-button");  // Replace with actual Add button locator for Vendor
    private By tableRows = By.xpath("//table[@id='vendor-table']/tbody/tr");  // Replace with actual table rows locator
    private By editButton = By.xpath("//table[@id='vendor-table']/tbody/tr/td[last()]/button[contains(text(),'Edit')]");
    private By viewButton = By.xpath("//table[@id='vendor-table']/tbody/tr/td[last()]/button[contains(text(),'View')]");

    // Constructor to initialize WebDriver
    public Vendor_Summary(WebDriver driver) {
        super(driver);
    }

    // Method to search for a vendor
    public void search(String searchText) {
        sendKeys(searchBox, searchText);  // Use the sendKeys method from BasePage
    }

    // Method to select the number of vendors to show per page
    public void selectEntriesPerPage(String count) {
        selectDropdownUsingVisibleText(showEntriesSelector, count);  // Use the selectDropdownByVisibleText from BasePage
    }

    // Method to click on the "Add Vendor" button
    public void clickAddButton() {
        click(addButton);  // Use the click method from BasePage
    }

    // Method to get the number of rows in the vendor table
    public int getTableRowCount() {
        List<WebElement> rows = (List<WebElement>) findElement(tableRows);  // Assuming findElements is available in BasePage
        return rows.size();
    }

    // Method to click the "Edit" button of a specific vendor row by index
    public void clickEditButton(int rowIndex) {
        WebElement row = findElement(By.xpath("//table[@id='vendor-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'Edit')]"));
        click((By) row);  // Use the click method from BasePage
    }

    // Method to click the "View" button of a specific vendor row by index
    public void clickViewButton(int rowIndex) {
        WebElement row = findElement(By.xpath("//table[@id='vendor-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'View')]"));
        click((By) row);  // Use the click method from BasePage
    }

    // Method to verify the vendor data in a specific row and column
    public boolean verifyTableData(int rowIndex, int columnIndex, String expectedText) {
        WebElement cell = findElement(By.xpath("//table[@id='vendor-table']/tbody/tr[" + rowIndex + "]/td[" + columnIndex + "]"));
        return cell.getText().equals(expectedText);  // Verify the text in the specified cell
    }
}
