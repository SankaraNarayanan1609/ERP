package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Indent;

import com.Vcidex.StoryboardSystems.Purchase.POJO.IndentData;
import com.Vcidex.StoryboardSystems.Purchase.PurchaseBasePage;
import com.Vcidex.StoryboardSystems.Purchase.PurchaseLogs;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PI_Add extends PurchaseBasePage {
    private final By branchDropdown      = By.id("branch-dropdown");
    private final By refNoField          = By.id("ref-no-field");
    private final By departmentField     = By.id("department-field");
    private final By requestedByDropdown = By.id("requested-by-dropdown");
    private final By priorityHighRadio   = By.id("priority-high-radio");
    private final By priorityLowRadio    = By.id("priority-low-radio");
    private final By remarksField        = By.id("remarks-field");
    private final By addButton           = By.id("add-button");
    private final By indentNoLocator     = By.cssSelector(".indent-number"); // adjust as needed

    public PI_Add(WebDriver driver) {
        super(driver);
    }

    /**
     * Complete “create indent” flow.
     * Returns the generated indent number.
     */
    public String createIndent(IndentData data) {
        // 1) start indent
        logger.info(PurchaseLogs.Inward.started());

        // 2) fill basics
        selectByText(branchDropdown, data.getBranchName());
        logger.info(PurchaseLogs.Inward.details(data.getIndentRefNo()));

        selectByText(requestedByDropdown, data.getRequestedBy());
        if ("high".equalsIgnoreCase(data.getPriority())) {
            click(priorityHighRadio, "Priority: High");
        } else {
            click(priorityLowRadio, "Priority: Low");
        }
        logger.info("🚩 Priority set to " + data.getPriority());

        // 3) remarks & items
        type(remarksField, data.getRemarks());
        click(addButton, "Add Indent");
        logger.info("📦 Added Indent item – Quantity: " + data.getQuantityRequested());

        // 4) submit & capture
        logger.info(PurchaseLogs.Inward.submitted());
        // assume clicking add is a submit here; otherwise add a separate submit button

        String indentNo = getText(indentNoLocator, "Indent Number");
        logger.info(PurchaseLogs.Inward.summaryValidated(indentNo));
        logger.info(PurchaseLogs.Inward.completed());

        return indentNo;
    }
}