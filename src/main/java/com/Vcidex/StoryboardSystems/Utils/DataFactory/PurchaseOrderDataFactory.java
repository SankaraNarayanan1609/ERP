package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.github.javafaker.Faker;
import com.Vcidex.StoryboardSystems.Purchase.POJO.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PurchaseOrderDataFactory {

    private final ApiMasterDataProvider apiProvider;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    // Master data lists
    private final List<String> branchNames;
    private final List<String> vendorNames;
    private final List<String> currencyCodes;
    private final List<String> termTemplates;
    private final List<String> dispatchModes = Arrays.asList("Courier", "Hand Delivery", "Email Copy");
    private final List<Product> productList;
    private final List<Tax> taxList;

    public PurchaseOrderDataFactory(ApiMasterDataProvider apiProvider) {
        this.apiProvider = apiProvider;

        // Fetch master data dynamically via API provider
        this.branchNames = safeList(apiProvider.getBranches());
        this.vendorNames = safeList(
                apiProvider.getVendors().stream()
                        .map(Vendor::getVendorName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
        this.currencyCodes = safeList(apiProvider.getCurrencies());
        this.termTemplates = safeList(apiProvider.getTermsAndConditions());
        this.productList = safeList(apiProvider.getProducts());
        this.taxList = safeList(apiProvider.getAllTaxObjects());
    }

    // Safely return non-null lists
    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    // Utility for random selection from list
    private <T> T randomFrom(List<T> list) {
        return list.isEmpty() ? null : list.get(random.nextInt(list.size()));
    }

    /** Picks a random LocalDate between now+startDays and now+endDays. */
    private static LocalDate randomDateBetween(int startDaysFromNow, int endDaysFromNow) {
        long start = LocalDate.now().plusDays(startDaysFromNow).toEpochDay();
        long end   = LocalDate.now().plusDays(endDaysFromNow).toEpochDay();
        long day   = ThreadLocalRandom.current().nextLong(start, end + 1);
        return LocalDate.ofEpochDay(day);
    }

    // Generate a random line item using a real product POJO and mapped tax
    private LineItem createRandomLineItem() {
        Product product = randomFrom(productList);
        if (product == null) return null;

        int quantity = faker.number().numberBetween(1, 25);
        BigDecimal price = BigDecimal.valueOf(
                faker.number().randomDouble(2, 1000, 50000)
        );
        // Discount percent (e.g., up to 15%)
        BigDecimal discountPct = BigDecimal.valueOf(
                faker.number().randomDouble(2, 0, 15)
        );
        // Discount amount = (price * quantity) * (discountPct/100)
        BigDecimal discountAmt = price.multiply(BigDecimal.valueOf(quantity))
                .multiply(discountPct)
                .divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);

        Tax randomTax = randomFrom(taxList);
        String taxPrefix = (randomTax != null && randomTax.getTaxPrefix() != null) ? randomTax.getTaxPrefix() : "GST 18%";
        // If tax is null, set default 0.18 (18%)
        BigDecimal taxRate = (randomTax != null && randomTax.getPercentage() != null)
                ? new BigDecimal(randomTax.getPercentage()).divide(BigDecimal.valueOf(100))
                : BigDecimal.valueOf(0.18);

        // Correct: Return the built object!
        LineItem item = LineItem.builder() // Cannot resolve method 'builder' in 'LineItem'
                .productGroup(product.getProductGroupName())
                .productCode(product.getProductCode())
                .productName(product.getProductName())
                .description(product.getProductDesc())
                .quantity(quantity)
                .price(price)
                .discountPct(discountPct)
                .discountAmt(discountAmt)
                .taxPrefix(taxPrefix)
                .taxRate(taxRate)
                // totalAmount will be computed next
                .build();

        item.computeTotal();
        return item;
    }

    private String randomTaxCode() {
        Tax tax = randomFrom(taxList);
        return tax != null ? tax.getTaxPrefix() : "GST 18%";
    }

    // Main method to generate random PurchaseOrderData using dynamic master data
    public PurchaseOrderData create(boolean isRenewal) {
        LocalDate poDate       = LocalDate.now();
        LocalDate expectedDate = randomDateBetween(3, 15);
        LocalDate renewalDate  = isRenewal ? randomDateBetween(30, 90) : null;
        String frequency       = isRenewal ? faker.options().option("Monthly", "Quarterly", "Half Yearly", "Yearly") : null;

        // Generate 1-3 random line items
        int lineItemCount = faker.number().numberBetween(1, 4);
        List<LineItem> lineItems = new ArrayList<>();
        for (int i = 0; i < lineItemCount; i++) {
            LineItem li = createRandomLineItem();
            if (li != null) lineItems.add(li);
        }

        PurchaseOrderData data = PurchaseOrderData.builder()
                .branchName(randomFrom(branchNames))
                .poRefNo("PO-" + faker.number().digits(6))
                .poDate(poDate)
                .expectedDate(expectedDate)
                .vendorName(randomFrom(vendorNames))
                .billTo(faker.address().fullAddress())
                .shipTo(faker.address().fullAddress())
                .requestedBy(faker.name().firstName())
                .requestorContactDetails(faker.phoneNumber().phoneNumber())
                .deliveryTerms("Deliver within 7 days")
                .paymentTerms("Net 30 days")
                .dispatchMode(randomFrom(dispatchModes))
                .currency(randomFrom(currencyCodes))
                .exchangeRate(BigDecimal.valueOf(1.0)) // Could fetch real rate if needed
                .coverNote("Please ensure quality check before dispatch.")
                .renewal(isRenewal)
                .renewalDate(renewalDate)
                .frequency(frequency)
                .lineItems(lineItems)
                .addOnCharges(BigDecimal.valueOf(faker.number().randomDouble(2,100,500)))
                .additionalDiscount(BigDecimal.valueOf(faker.number().randomDouble(2,5,20)))
                .freightCharges(BigDecimal.valueOf(faker.number().randomDouble(2,100,300)))
                .additionalTax(randomTaxCode())
                .roundOff(BigDecimal.ZERO)
                .termsAndConditions(randomFrom(termTemplates))
                .termsEditorText("Ensure packaging standards are met.")
                .build();

        data.computeNetAmount();
        data.computeGrandTotal();

        // (Optional) Add debug log
        System.out.println("[DEBUG] Generated PurchaseOrderData: " + data);

        return data;
    }
}