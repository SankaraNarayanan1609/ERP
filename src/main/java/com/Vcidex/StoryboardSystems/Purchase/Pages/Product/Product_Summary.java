package com.Vcidex.StoryboardSystems.Purchase.Pages.Product;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class Product_Summary extends BasePage {

    // Locators for Product Summary page elements
    private By searchBox = By.id("product-search-bar");  // Replace with actual search bar locator for Product
    private By showEntriesSelector = By.id("product-show-entries-selector");  // Replace with actual selector for Product
    private By addButton = By.id("product-add-button");  // Replace with actual Add button locator for Product
    private By tableRows = By.xpath("//table[@id='product-table']/tbody/tr");  // Replace with actual table rows locator
    private By editButton = By.xpath("//table[@id='product-table']/tbody/tr/td[last()]/button[contains(text(),'Edit')]");
    private By viewButton = By.xpath("//table[@id='product-table']/tbody/tr/td[last()]/button[contains(text(),'View')]");

    // Constructor to initialize WebDriver
    public Product_Summary(WebDriver driver) {
        super(driver);
    }

    // Method to search for a product
    public void search(String searchText) {
        sendKeys(searchBox, searchText);  // Enter text into the search bar
    }

    // Method to select the number of products to show per page
    public void selectEntriesPerPage(String count) {
        selectDropdownUsingVisibleText(showEntriesSelector, count);  // Select from dropdown
    }

    // Method to click on the "Add Product" button
    public void clickAddButton() {
        click(addButton);  // Click the "Add" button
    }

    // Method to get the number of rows in the product table
    public int getTableRowCount() {
        List<WebElement> rows = (List<WebElement>) findElements(tableRows);  // Get all rows from the product table
        return rows.size();
    }

    // Method to click the "Edit" button of a specific product row by index
    public void clickEditButton(int rowIndex) {
        WebElement row = findElement(By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'Edit')]"));
        click((By) row);  // Click the Edit button in the specified row
    }

    // Method to find a list of elements
    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    // Method to click the "View" button of a specific product row by index
    public void clickViewButton(int rowIndex) {
        WebElement row = findElement(By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'View')]"));
        click((By) row);  // Click the View button in the specified row
    }

    // Method to verify the product data in a specific row and column
    public boolean verifyTableData(int rowIndex, int columnIndex, String expectedText) {
        WebElement cell = findElement(By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[" + columnIndex + "]"));
        return cell.getText().equals(expectedText);  // Verify the text in the specified cell
    }
}
