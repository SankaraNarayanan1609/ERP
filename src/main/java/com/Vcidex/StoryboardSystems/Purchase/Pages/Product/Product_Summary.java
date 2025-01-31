package com.Vcidex.StoryboardSystems.Purchase.Pages.Product;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

public class Product_Summary extends BasePage {

    // Locators for Product Summary page elements
    private By searchBox = By.id("product-search-bar");
    private By showEntriesSelector = By.id("product-show-entries-selector");
    private By addButton = By.id("product-add-button");
    private By tableRows = By.xpath("//table[@id='product-table']/tbody/tr");
    private By editButton = By.xpath("//table[@id='product-table']/tbody/tr/td[last()]/button[contains(text(),'Edit')]");
    private By viewButton = By.xpath("//table[@id='product-table']/tbody/tr/td[last()]/button[contains(text(),'View')]");

    // Constructor to initialize WebDriver
    public Product_Summary(WebDriver driver) {
        super(driver);
    }

    // Method to search for a product
    public void search(String searchText) {
        enterTextUsingFollowingSibling(searchBox, searchText);
    }

    // Method to select the number of products to show per page
    public void selectEntriesPerPage(String count) {
        selectDropdownUsingVisibleText(showEntriesSelector, count);
    }

    // ✅ Fixed: Click "Add Product" Button
    public void clickAddButton() {
        WebElement addButtonElement = findElement(addButton); // ✅ Convert `By` to `WebElement`
        click(addButtonElement, true); // ✅ Now it correctly passes a WebElement
    }

    // Method to get the number of rows in the product table
    public int getTableRowCount() {
        List<WebElement> rows = (List<WebElement>) findElements(tableRows);
        return rows.size();
    }

    // ✅ Fixed: Click the "Edit" button of a specific product row by index
    public void clickEditButton(int rowIndex) {
        WebElement editButton = findElement(By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'Edit')]"));
        click(editButton, true); // ✅ Correctly clicks the WebElement
    }

    // Method to find a list of elements
    public List<WebElement> findElements(By locator) {
        return driver.findElements(locator);
    }

    // ✅ Fixed: Click the "View" button of a specific product row by index
    public void clickViewButton(int rowIndex) {
        WebElement viewButton = findElement(By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'View')]"));
        click(viewButton, true); // ✅ Correctly clicks the WebElement
    }

    // Method to verify the product data in a specific row and column
    public boolean verifyTableData(int rowIndex, int columnIndex, String expectedText) {
        WebElement cell = findElement(By.xpath("//table[@id='product-table']/tbody/tr[" + rowIndex + "]/td[" + columnIndex + "]"));
        return cell.getText().equals(expectedText);
    }
}