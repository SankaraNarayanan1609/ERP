// src/main/java/com/Vcidex/StoryboardSystems/Purchase/Commands/PaymentCommand.java
package com.Vcidex.StoryboardSystems.Purchase.Flow.Commands;

import java.math.BigDecimal;

import org.openqa.selenium.WebDriver;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.Steps.StepCommand;
import com.Vcidex.StoryboardSystems.Purchase.Pages.PaymentPage;
import com.Vcidex.StoryboardSystems.Utils.CurrencyConverter;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;

public class PaymentCommand implements StepCommand {
    @Override
    public void execute(WebDriver driver,
                        PurchaseOrderData data,
                        PurchaseScenario scen) {
        PaymentPage page = new PaymentPage(driver);
        page.selectVendor(data);
        page.selectPaymentStyle(scen.getPaymentType());
        page.submitPayment();

        if (!"INR".equals(data.getVendorCurrency())) {
            BigDecimal displayed = page.getDisplayedConvertedAmount();
            BigDecimal expected = CurrencyConverter.convert(
                    data.getInvoiceTotal(),
                    data.getVendorCurrency()
            );
            ValidationLogger.verify(
                    displayed.compareTo(expected) == 0,
                    "Currency conversion assertion for " + data.getVendorCurrency()
            );
        }
    }
}