package com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order;

import com.Vcidex.StoryboardSystems.Common.BasePage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class PO_Mail extends BasePage {

    // Constructor
    public PO_Mail(WebDriver driver) {
        super(driver);
    }

    // Unique Elements for PO_Mail
    @FindBy(id = "FromDropdown")
    private WebElement fromDropdown;

    @FindBy(id = "ToField")
    private WebElement toField;

    @FindBy(id = "CCField")
    private WebElement ccField;

    @FindBy(id = "SubjectInput")
    private WebElement subjectInput;

    @FindBy(id = "UploadField")
    private WebElement uploadField;

    @FindBy(id = "BodyDropdown")
    private WebElement bodyDropdown;

    @FindBy(id = "BodyInput")
    private WebElement bodyInput;

    @FindBy(id = "SendButton")
    private WebElement sendButton;

    @FindBy(id = "BackButton")
    private WebElement backButton;

    @FindBy(id = "BeforeButton")
    private WebElement beforeButton;

    // Methods for PO_Mail

    // Selects a value from the 'From' dropdown
    public void selectFrom(String from) {
        fromDropdown.sendKeys(from);
    }

    // Returns the autofetched 'To' field value
    public String getToFieldValue() {
        return toField.getAttribute("value");
    }

    // Enters an email into the 'CC' field
    public void enterCC(String cc) {
        ccField.sendKeys(cc);
    }

    // Enters text in the 'Subject' field
    public void enterSubject(String subject) {
        subjectInput.sendKeys(subject);
    }

    // Returns the file name in the autofetched 'Upload' field
    public String getUploadedFileName() {
        return uploadField.getText();
    }

    // Selects a template from the 'Body' dropdown
    public void selectBodyTemplate(String template) {
        bodyDropdown.sendKeys(template);
    }

    // Enters custom text in the 'Body' input field
    public void enterBodyText(String bodyText) {
        bodyInput.sendKeys(bodyText);
    }

    // Clicks the 'Send' button to send the email
    public void clickSendButton() {
        sendButton.click();
    }

    // Clicks the 'Back' button to navigate back
    public void clickBackButton() {
        backButton.click();
    }

    // Clicks the 'Before' button to navigate to the previous page
    public void clickBeforeButton() {
        beforeButton.click();
    }
}

