package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.github.javafaker.Faker;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PurchaseOrderDataFactory {
    private static final Faker faker = new Faker();
    private static final List<String> BRANCHES    = Arrays.asList("Chennai","Bangalore","Mumbai","Delhi");
    private static final List<String> VENDORS     = Arrays.asList("Reliance Pvt Ltd","TCS Suppliers","HCL Traders","Infosys Materials");
    private static final List<String> CURRENCIES  = Arrays.asList("INR","USD","EUR");
    private static final List<String> FREQUENCIES = Arrays.asList("Monthly","Quarterly","Half Yearly","Yearly");
    private static final List<String> TERMS_LIST  = Arrays.asList("Standard T&C","Custom Clause A","Clause B");

    /** Picks a random LocalDate between now+startDays and now+endDays. */
    private static LocalDate randomDateBetween(int startDaysFromNow, int endDaysFromNow) {
        long start = LocalDate.now().plusDays(startDaysFromNow).toEpochDay();
        long end   = LocalDate.now().plusDays(endDaysFromNow).toEpochDay();
        long day   = ThreadLocalRandom.current().nextLong(start, end + 1);
        return LocalDate.ofEpochDay(day);
    }

    public static PurchaseOrderData createRandomPOData(boolean isRenewal) {
        // core dates
        LocalDate poDate       = LocalDate.now();
        LocalDate expectedDate = randomDateBetween(3, 15);
        LocalDate renewalDate  = isRenewal
                ? randomDateBetween(30, 90)
                : null;
        String frequency       = isRenewal
                ? faker.options().option(FREQUENCIES.toArray(new String[0]))
                : null;

        // build up the object
        PurchaseOrderData data = PurchaseOrderData.builder()
                .branchName(faker.options().option(BRANCHES.toArray(new String[0])))
                .poRefNo("PO-" + faker.number().digits(6))
                .poDate(poDate)
                .expectedDate(expectedDate)
                .vendorName(faker.options().option(VENDORS.toArray(new String[0])))
                .billTo(faker.address().fullAddress())
                .shipTo(faker.address().fullAddress())
                .requestedBy(faker.name().firstName())
                .requestorContactDetails(faker.phoneNumber().phoneNumber())
                .deliveryTerms("Deliver within 7 days")
                .paymentTerms("Net 30 days")
                .dispatchMode(faker.options().option("Courier","Hand Delivery","Email Copy"))
                .currency(faker.options().option(CURRENCIES.toArray(new String[0])))
                // you could fetch exchange rate from your MasterDataProvider here…
                .exchangeRate(BigDecimal.valueOf(1.0))
                .coverNote("Please ensure quality check before dispatch.")
                .renewal(isRenewal)
                .renewalDate(renewalDate)
                .frequency(frequency)
                // if you have lineItems, you’d set them here; otherwise skip
                .lineItems(List.of())
                .addOnCharges(BigDecimal.valueOf(faker.number().randomDouble(2,100,500)))
                .additionalDiscount(BigDecimal.valueOf(faker.number().randomDouble(2,5,20)))
                .freightCharges(BigDecimal.valueOf(faker.number().randomDouble(2,100,300)))
                .additionalTax("GST 18%")
                .roundOff(BigDecimal.ZERO)   // will be recalculated below
                .termsAndConditions(faker.options().option(TERMS_LIST.toArray(new String[0])))
                .termsEditorText("Ensure packaging standards are met.")
                .build();

        // compute the derived totals
        data.computeNetAmount();
        data.computeGrandTotal();

        return data;
    }
}