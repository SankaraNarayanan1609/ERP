package com.Vcidex.StoryboardSystems.Inventory.Pages.Inward;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.ErrorLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.UIActionLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class MaterialInwardPage extends BasePage {
    private final WebDriverWait wait;

    // --- New locator for Add Inward button ---
    private final By addInwardBtn        = By.cssSelector("button[data-bs-target='#myModaladd']");
    private final By loadingOverlay      = By.cssSelector(".spinner-overlay, .modal-backdrop");
    private final By submitBtn           = By.cssSelector("button.btn-success");
    private final By backBtn             = By.cssSelector("button.btn-primary");

    private final By branchInput         = By.cssSelector("input[formcontrolname='branch_name']");
    private final By dcNoInput           = By.cssSelector("input[formcontrolname='dc_no']");
    private final By datePicker          = By.cssSelector("input[formcontrolname='grn_date']");
    private final By expectedDatePicker  = By.cssSelector("input[formcontrolname='expected_date']");

    private final By dispatchSelect      = By.cssSelector("ng-select[formcontrolname='modeof_dispatch']");
    private final By dispatchFilter      = By.cssSelector("ng-select[formcontrolname='modeof_dispatch'] input");
    private final By trackingInput       = By.cssSelector("input[formcontrolname='deliverytracking']");
    private final By boxesInput          = By.cssSelector("input[formcontrolname='no_box']");

    private final By fileInput           = By.cssSelector("input[type='file'][id='img']");
    private final By tableRows           = By.cssSelector("table#addgrn_lists tbody tr");

    public MaterialInwardPage(WebDriver driver) {
        super(driver);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void waitForLoad() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(loadingOverlay));
        wait.until(ExpectedConditions.visibilityOfElementLocated(branchInput));
    }

    // new locator for the “Select Purchase Order” header
    private final By selectPoHeader = By.xpath("//h3[normalize-space(text())='Select Purchase Order']");

    /**
     * Verifies we’re on the “Select Purchase Order” screen.
     */
    public void assertOnSelectPurchaseOrder(ExtentTest node) {
        PerformanceLogger.start("MaterialInward.assertOnSelectPurchaseOrder");
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(selectPoHeader));
            node.pass("✅ On 'Select Purchase Order' screen");
        } catch (Exception e) {
            ErrorLogger.logException(e, "MaterialInward.assertOnSelectPurchaseOrder", driver);
            throw e;
        } finally {
            PerformanceLogger.end("MaterialInward.assertOnSelectPurchaseOrder");
        }
    }

    /**
     * Finds the row whose PO Ref No matches `poRef` and clicks its Select button.
     */
    public void selectPurchaseOrder(String poRef, ExtentTest node) {
        PerformanceLogger.start("MaterialInward.selectPurchaseOrder");
        try {
            By selectBtn = By.xpath(
                    "//table[@id='grn_list']//tbody/tr[" +
                            "td[4][normalize-space(text())='" + poRef + "']" +
                            "]//button[@title='Select']"
            );
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(selectBtn));
            UIActionLogger.click(driver, selectBtn, "Select PO '" + poRef + "'", node);
            node.pass("✅ Selected Purchase Order: " + poRef);
        } catch (Exception e) {
            ErrorLogger.logException(e, "MaterialInward.selectPurchaseOrder", driver);
            throw e;
        } finally {
            PerformanceLogger.end("MaterialInward.selectPurchaseOrder");
        }
    }
    /**
     * Clicks the "Add Inward" button to open the add modal.
     */
    public void clickAddInward(ExtentTest node) {
        PerformanceLogger.start("MaterialInward.clickAddInward");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(addInwardBtn));
            UIActionLogger.click(driver, addInwardBtn, "Click Add Inward", node);
            node.pass("✅ Clicked Add Inward button");
        } catch (Exception e) {
            ErrorLogger.logException(e, "MaterialInward.clickAddInward", driver);
            throw e;
        } finally {
            PerformanceLogger.end("MaterialInward.clickAddInward");
        }
    }



    public void fillHeader(String dcNo, String grnDate, String expectedDate, ExtentTest node) {
        PerformanceLogger.start("MaterialInward.fillHeader");
        try {
            waitForLoad();
            UIActionLogger.type(driver, dcNoInput, dcNo, "Delivery No", node);
            FlatpickrDatePicker.pickFlatpickrDate(driver, datePicker, grnDate, "GRN Date");
            driver.findElement(datePicker).sendKeys(Keys.ESCAPE);
            FlatpickrDatePicker.pickFlatpickrDate(driver, expectedDatePicker, expectedDate, "Expected Date");
            driver.findElement(expectedDatePicker).sendKeys(Keys.ESCAPE);
            node.pass("✅ Header filled");
        } catch (Exception e) {
            ErrorLogger.logException(e, "MaterialInward.fillHeader", driver);
            throw e;
        } finally {
            PerformanceLogger.end("MaterialInward.fillHeader");
        }
    }

    public void selectDispatchMode(String mode, ExtentTest node) {
        PerformanceLogger.start("MaterialInward.selectDispatch");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(dispatchSelect));
            UIActionLogger.click(driver, dispatchSelect, "Open Dispatch Select", node);
            WebElement filter = wait.until(ExpectedConditions.visibilityOfElementLocated(dispatchFilter));
            filter.sendKeys(mode, Keys.ENTER);
            node.pass("✅ Dispatch mode set to '" + mode + "'");
        } catch (Exception e) {
            ErrorLogger.logException(e, "MaterialInward.selectDispatch", driver);
            throw e;
        } finally {
            PerformanceLogger.end("MaterialInward.selectDispatch");
        }
    }

    public void fillDispatchDetails(String tracking, String boxes, ExtentTest node) {
        UIActionLogger.type(driver, trackingInput, tracking, "Delivery Tracking", node);
        UIActionLogger.type(driver, boxesInput, boxes, "No of Boxes", node);
    }

    public void assertRowCount(int expected, ExtentTest node) {
        int actual = driver.findElements(tableRows).size();
        ValidationLogger.assertEquals("Table row count", String.valueOf(expected), String.valueOf(actual), driver);
        node.pass("✅ Found " + expected + " rows");
    }

    public void enterReceivedQty(int row, String qty, ExtentTest node) {
        PerformanceLogger.start("MaterialInward.enterReceivedQty");
        try {
            By loc = By.xpath("(//table[@id='addgrn_lists']//tbody/tr)[" + row + "]//input");
            WebElement in = wait.until(ExpectedConditions.elementToBeClickable(loc));
            in.clear(); in.sendKeys(qty);
            node.pass("✅ ReceivedQty[" + row + "]=" + qty);
        } catch (Exception e) {
            ErrorLogger.logException(e, "MaterialInward.enterReceivedQty", driver);
            throw e;
        } finally {
            PerformanceLogger.end("MaterialInward.enterReceivedQty");
        }
    }

    /**
     * Fills the entire inward form and submits it.
     */
    public void fillForm(MaterialInwardData d, ExtentTest node) {
        clickAddInward(node);
        fillHeader(d.getDcNo(), d.getGrnDate(), d.getExpectedDate(), node);
        selectDispatchMode(d.getDispatchMode(), node);
        fillDispatchDetails(d.getTrackingNo(), d.getNoOfBoxes(), node);
        assertRowCount(d.getReceivedQtyByRow().size(), node);
        d.getReceivedQtyByRow().forEach((row, val) -> enterReceivedQty(row, val, node));
        clickSubmit(node);
    }

    public void clickSubmit(ExtentTest node) {
        PerformanceLogger.start("MaterialInward.clickSubmit");
        try {
            wait.until(ExpectedConditions.elementToBeClickable(submitBtn));
            UIActionLogger.click(driver, submitBtn, "Submit Material Inward", node);
            node.pass("✅ Submit clicked");
        } catch (Exception e) {
            ErrorLogger.logException(e, "MaterialInward.clickSubmit", driver);
            throw e;
        } finally {
            PerformanceLogger.end("MaterialInward.clickSubmit");
        }
    }

    public void clickBack(ExtentTest node) {
        PerformanceLogger.start("MaterialInward.clickBack");
        try {
            UIActionLogger.click(driver, backBtn, "Back to list", node);
            node.pass("✅ Back clicked");
        } catch (Exception e) {
            ErrorLogger.logException(e, "MaterialInward.clickBack", driver);
            throw e;
        } finally {
            PerformanceLogger.end("MaterialInward.clickBack");
        }
    }
}