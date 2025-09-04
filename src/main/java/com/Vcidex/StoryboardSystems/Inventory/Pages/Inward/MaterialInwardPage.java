package com.Vcidex.StoryboardSystems.Inventory.Pages.Inward;

import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.group;
import static com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.step;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Utils.FlatpickrDatePicker;
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger.Layer;
import com.Vcidex.StoryboardSystems.Utils.Logger.PerformanceLogger;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class MaterialInwardPage extends BasePage {
    private static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Locators
    private final By addInwardBtn       = By.cssSelector("button[data-bs-target='#myModaladd']");
    private final By branchInput        = By.cssSelector("input[formcontrolname='branch_name']");
    private final By dcNoInput          = By.cssSelector("input[formcontrolname='dc_no']");
    private final By datePicker         = By.cssSelector("input[formcontrolname='grn_date']");
    private final By expectedDatePicker = By.cssSelector("input[formcontrolname='expected_date']");
    private final By trackingInput      = By.cssSelector("input[formcontrolname='deliverytracking']");
    private final By boxesInput         = By.cssSelector("input[formcontrolname='no_box']");
    private final By submitBtn          = By.cssSelector("app-ims-trn-grninwardaddsubmit form .text-center.my-4 button.btn-success");
    private final By backBtn            = By.cssSelector("button.btn-primary");
    private final By selectPoHeader     = By.xpath("//h3[normalize-space(text())='Select Purchase Order']");

    public MaterialInwardPage(WebDriver driver) { super(driver); }

    public void assertOnSelectPurchaseOrder(ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.assertOnSelectPurchaseOrder");
        step(Layer.UI, "Verify Select Purchase Order screen", () -> {
            waitUntilVisible(selectPoHeader, "Select Purchase Order");
            return null;
        });
        PerformanceLogger.end("MaterialInward.assertOnSelectPurchaseOrder");
    }

    public void selectPurchaseOrder(String poRef, ExtentTest node) {
        ReportManager.setTest(node);
        By eitherTable = By.xpath(
                "//table[@id='purchaseOrderList' or @id='grn_list']" +
                        "//tbody/tr[td[4][normalize-space()='"+poRef+"']]//button[@title='Select' or normalize-space()='Select']"
        );
        click(eitherTable, "Select PO '" + poRef + "'");
    }

    public void clickAddInward(ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.clickAddInward");
        step(Layer.UI, "Click Add Inward", () -> {
            waitForOverlayClear();
            waitUntilVisible(addInwardBtn, "Add Inward button");
            scrollIntoView(addInwardBtn);
            click(addInwardBtn, "Add Inward");
            return null;
        });
        PerformanceLogger.end("MaterialInward.clickAddInward");
    }

    public void fillHeader(MaterialInwardData d, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.fillHeader");
        step(Layer.UI, "Fill Inward header", () -> {
            waitForOverlayClear();
            scrollIntoView(dcNoInput);
            type(dcNoInput, d.getDcNo(), "Delivery No"); // Cannot resolve method 'type' in 'MaterialInwardPage'
            FlatpickrDatePicker.pickDateAndClose(driver, datePicker,         d.getGrnDate(),      "GRN Date");
            FlatpickrDatePicker.pickDateAndClose(driver, expectedDatePicker, d.getExpectedDate(), "Expected Date");
            return null;
        });
        PerformanceLogger.end("MaterialInward.fillHeader");
    }

    public void selectDispatchMode(String mode, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.selectDispatch");
        step(Layer.UI, "Select dispatch mode = " + mode, () -> {
            selectFromNgSelect("modeof_dispatch", mode);
            return null;
        });
        PerformanceLogger.end("MaterialInward.selectDispatch");
    }

    public void fillDispatchDetails(MaterialInwardData d, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.fillDispatchDetails");
        group("Fill dispatch details", () -> {
            type(trackingInput, d.getTrackingNo(), "Delivery Tracking"); // Cannot resolve method 'type' in 'MaterialInwardPage'
            type(boxesInput,    d.getNoOfBoxes(),  "No of Boxes"); // Cannot resolve method 'type' in 'MaterialInwardPage'
        });
        PerformanceLogger.end("MaterialInward.fillDispatchDetails");
    }

    public void enterReceivedQty(int row, String qty, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.enterReceivedQty");
        step(Layer.UI, "Enter Received Qty[" + row + "] = " + qty, () -> {
            By loc = By.xpath("(//table[@id='addgrn_lists']//tbody/tr)[" + row + "]//input");
            type(loc, qty, "ReceivedQty[" + row + "]");
            return null;
        });
        PerformanceLogger.end("MaterialInward.enterReceivedQty");
    }

    public void clickSubmit(ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.clickSubmit");
        step(Layer.UI, "Submit Material Inward", () -> {
            waitForOverlayClear();
            WebElement btn = waitUntilVisible(submitBtn, "Submit button");
            scrollIntoView(submitBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
            return null;
        });
        PerformanceLogger.end("MaterialInward.clickSubmit");
    }

    /** Only count rows that actually have an &lt;input&gt; (real line-items). */
    public void assertRowCount(int expected, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.assertRowCount");
        step(Layer.UI, "Verify real row-count = " + expected, () -> {
            List<WebElement> rows = driver.findElements(
                    By.xpath("//table[@id='addgrn_lists']/tbody/tr[.//input]")
            );
            ValidationLogger.assertEquals(
                    "Table row count",
                    String.valueOf(expected),
                    String.valueOf(rows.size()),
                    node
            );
            return null;
        });
        PerformanceLogger.end("MaterialInward.assertRowCount");
    }

    public void clickBack(ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.clickBack");
        step(Layer.UI, "Click Back to list", () -> {
            click(backBtn, "Back");
            return null;
        });
        PerformanceLogger.end("MaterialInward.clickBack");
    }

    // ─────────────────────────────────────────────────────────
    // ✅ Added: used by your test — creates a full Inward entry
    // ─────────────────────────────────────────────────────────
    public void createInwardEntry(MaterialInwardData data, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.createInwardEntry");
        group("Create Material Inward Entry", () -> {
            clickAddInward(node);
            fillHeader(data, node);
            selectDispatchMode(data.getDispatchMode(), node);
            fillDispatchDetails(data, node);
            fillInwardDetails(data, node);
            clickSubmit(node);
            clickBack(node);
        });
        PerformanceLogger.end("MaterialInward.createInwardEntry");
    }

    // ─────────────────────────────────────────────────────────
    // ✅ Added: helper used by createInwardEntry
    // ─────────────────────────────────────────────────────────
    public void fillInwardDetails(MaterialInwardData data, ExtentTest node) {
        ReportManager.setTest(node);
        PerformanceLogger.start("MaterialInward.fillInwardDetails");
        step(Layer.UI, "Fill received quantity for all PO rows", () -> {
            Map<Integer, String> qtyMap = data.getReceivedQtyByRow();
            for (Map.Entry<Integer, String> entry : qtyMap.entrySet()) {
                enterReceivedQty(entry.getKey(), entry.getValue(), node);
            }
            return null;
        });
        PerformanceLogger.end("MaterialInward.fillInwardDetails");
    }

    // in MaterialInwardPage.java
    public String submitAndCapture(ExtentTest node) {
        ReportManager.setTest(node);
        clickSubmit(node);
        // Reuse BasePage extractor with GRN/INWARD patterns
        return captureRefByRegex(
                "(GRN[-/ ]?\\d{4,})",
                "(INWARD[-/ ]?\\d{4,})",
                "(Inward\\s*No\\s*[:#]\\s*\\S+)"
        );
    }
}