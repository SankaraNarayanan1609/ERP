package com.Vcidex.StoryboardSystems.Purchase.Pages.Product;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.Collections; // ✅ Import for empty map
import java.util.List;

public class Product_Summary extends BasePage {

    // Locators for Product Summary page elements
    private By searchBox = By.id("product-search-bar");
    private By showEntriesSelector = By.id("product-show-entries-selector");
    private By addButton = By.id("product-add-button");
    private By tableRows = By.xpath("//table[@id='product-table']/tbody/tr");
    private By editButton = By.xpath("//table[@id='product-table']/tbody/tr/td[last()]/button[contains(text(),'Edit')]");
    private By viewButton = By.xpath("//table[@id='product-table']/tbody/tr/td[last()]/button[contains(text(),'View')]");

    // ✅ Fixed: Constructor to initialize WebDriver with scenario data
    public Product_Summary(WebDriver driver) {
        super(driver, Collections.emptyMap()); // // Required type:WebDriver , Provided:Map
    }

    // Method to search for a product
    public void search(String searchText) {
        sendText(searchBox, searchText);//Cannot resolve method 'sendText' in 'Product_Summary'
    }

    // Method to select the number of products to show per page
    public void selectEntriesPerPage(String count) {
        selectDropdownUsingVisibleText(showEntriesSelector, count);
    }

    // ✅ Fixed: Click "Add Product" Button
    public void clickAddButton() {
        click(addButton); // ✅ Removed extra argument
    }

    // Method to get the number of rows in the product table
    public int getTableRowCount() {
        List<WebElement> rows = findElements(tableRows);
        return rows.size();
    }

    // ✅ Fixed: Click the "Edit" button of a specific product row by index
    public void clickEditButton(int rowIndex) {
        By editButtonLocator = By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'Edit')]");
        click(editButtonLocator); // ✅ Removed extra argument
    }

    // Method to find a list of elements
    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    // ✅ Fixed: Click the "View" button of a specific product row by index
    public void clickViewButton(int rowIndex) {
        By viewButtonLocator = By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'View')]");
        click(viewButtonLocator); // ✅ Removed extra argument
    }

    // Method to verify the product data in a specific row and column
    public boolean verifyTableData(int rowIndex, int columnIndex, String expectedText) {
        WebElement cell = findElement(By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[" + columnIndex + "]"));
        return cell.getText().equals(expectedText);
    }
}
