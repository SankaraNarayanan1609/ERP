//package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent;
//
//import com.Vcidex.StoryboardSystems.Common.BasePage;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.WebElement;
//import java.util.List;
//
//public class PI_Summary extends BasePage {
//
//    // Locators for PI Summary page elements
//    private By searchBox = By.id("pi-search-bar");  // Replace with actual search bar locator for PI
//    private By showEntriesSelector = By.id("pi-show-entries-selector");  // Replace with actual selector for PI
//    private By addButton = By.id("pi-add-button");  // Replace with actual Add button locator for PI
//    private By tableRows = By.xpath("//table[@id='pi-table']/tbody/tr");  // Replace with actual table rows locator
//    private By editButton = By.xpath("//table[@id='pi-table']/tbody/tr/td[last()]/button[contains(text(),'Edit')]");
//    private By viewButton = By.xpath("//table[@id='pi-table']/tbody/tr/td[last()]/button[contains(text(),'View')]");
//
//    // Constructor to initialize WebDriver
//    public PI_Summary(WebDriver driver) {
//        super(driver);
//    }
//
//    // Method to search for a PI
//    //public void search(String searchText) {
//        //sendKeys(searchBox, searchText);  // Enter text into the search bar
//    }
//
//    // Method to select the number of PIs to show per page
//    public void selectEntriesPerPage(String count) {
//        selectDropdownUsingVisibleText(showEntriesSelector, count);// Select from dropdown
//    }
//
//    // Method to click on the "Add PI" button
//    public void clickAddButton() {
//        click(addButton);  // Click the "Add" button
//    }
//
//    // Method to get the number of rows in the PI table
//    public int getTableRowCount() {
//        List<WebElement> rows = findElements(tableRows);  // Get all rows from the PI table
//        return rows.size();
//    }
//
//    // Method to click the "Edit" button of a specific PI row by index
//    public void clickEditButton(int rowIndex) {
//        WebElement row = findElement(By.xpath("//table[@id='pi-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'Edit')]"));
//        row.click();// Click the Edit button in the specified row
//    }
//
//    // Method to click the "View" button of a specific PI row by index
//    public void clickViewButton(int rowIndex) {
//        WebElement row = findElement(By.xpath("//table[@id='pi-table']/tbody/tr[" + rowIndex + "]/td[last()]/button[contains(text(),'View')]"));
//        row.click();// Click the View button in the specified row
//    }
//
//    // Method to verify the PI data in a specific row and column
//    public boolean verifyTableData(int rowIndex, int columnIndex, String expectedText) {
//        WebElement cell = findElement(By.xpath("//table[@id='pi-table']/tbody/tr[" + rowIndex + "]/td[" + columnIndex + "]"));
//        return cell.getText().equals(expectedText);  // Verify the text in the specified cell
//    }
//
//    // Method to find a list of elements (helper method for findElements)
//    public List<WebElement> findElements(By locator) {
//        return driver.findElements(locator);  // Returns a list of matching elements
//    }
//}
