package com.Vcidex.StoryboardSystems.Utils.Data.DataFactory;

import com.github.javafaker.Faker;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PurchaseOrderDataFactory {

    private static final Faker faker = new Faker();
    private static final List<String> BRANCHES = Arrays.asList("Chennai", "Bangalore", "Mumbai", "Delhi");
    private static final List<String> VENDORS = Arrays.asList("Reliance Pvt Ltd", "TCS Suppliers", "HCL Traders", "Infosys Materials");
    private static final List<String> CURRENCIES = Arrays.asList("INR", "USD", "EUR");
    private static final List<String> FREQUENCIES = Arrays.asList("Monthly", "Quarterly", "Half Yearly", "Yearly");
    private static final List<String> TERMS_LIST = Arrays.asList("Standard T&C", "Custom Clause A", "Clause B");

    private static Date randomFutureDate(int daysFromNowStart, int daysFromNowEnd) {
        long startMillis = System.currentTimeMillis() + (daysFromNowStart * 24L * 60 * 60 * 1000);
        long endMillis = System.currentTimeMillis() + (daysFromNowEnd * 24L * 60 * 60 * 1000);
        return new Date(ThreadLocalRandom.current().nextLong(startMillis, endMillis));
    }

    public static PurchaseOrderData createRandomPOData(boolean isRenewal) {
        Date poDate = new Date(); // today
        Date expectedDate = randomFutureDate(3, 15);
        Date renewalDate = isRenewal ? randomFutureDate(30, 90) : null;
        String frequency = isRenewal ? getRandom(FREQUENCIES) : null;

        PurchaseOrderData data = new PurchaseOrderData();
        data.setBranchName(getRandom(BRANCHES));
        data.setPoRefNo("PO-" + faker.number().digits(6));
        data.setPoDate(poDate);
        data.setExpectedDate(expectedDate);
        data.setVendorName(getRandom(VENDORS));
        data.setBillTo(faker.address().fullAddress());
        data.setShipTo(faker.address().fullAddress());
        data.setRequestedBy(faker.name().firstName());
        data.setDeliveryTerms("Deliver within 7 days");
        data.setPaymentTerms("Net 30 days");
        data.setRequestorContactDetails(faker.phoneNumber().phoneNumber());
        data.setDespatchMode(faker.options().option("Courier", "Hand Delivery", "Email Copy"));
        data.setCurrency(getRandom(CURRENCIES));
        data.setCoverNote("Please ensure quality check before dispatch.");

        if (isRenewal) {
            data.setRenewalDate(renewalDate);
            data.setFrequency(frequency);
        }

        data.setTermsAndConditions(getRandom(TERMS_LIST));
        data.setTermsAndConditionsEditor("Ensure packaging standards are met.");

        // Financials
        data.setAddOnCharges(faker.number().randomDouble(2, 100, 500));
        data.setAdditionalDiscount(faker.number().randomDouble(2, 5, 20));
        data.setFreightCharges(faker.number().randomDouble(2, 100, 300));
        data.setAdditionalTax("GST 18%");
        data.setRoundOff(faker.number().randomDouble(2, -2, 2));

        return data;
    }

    private static <T> T getRandom(List<T> list) {
        return list.get(faker.random().nextInt(list.size()));
    }
}
