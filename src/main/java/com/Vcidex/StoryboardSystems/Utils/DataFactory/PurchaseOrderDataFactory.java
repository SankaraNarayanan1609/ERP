// File: src/main/java/com/Vcidex/StoryboardSystems/Utils/DataFactory/PurchaseOrderDataFactory.java
package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.POJO.*;
import com.github.javafaker.Faker;
import lombok.Builder;  // make sure your PurchaseOrderData uses @Builder
import lombok.Singular;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PurchaseOrderDataFactory {

    private final ApiMasterDataProvider apiProvider;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    private final List<String>   branchNames;
    private final List<Vendor>   vendorList;
    private final List<String>   vendorNames;
    private final List<String>   currencyCodes;
    private final List<String>   termTemplates;
    private final List<String>   dispatchModes = Arrays.asList("Courier", "Hand Delivery", "Email Copy");
    private final List<Product>  productList;
    private final List<Tax>      taxList;
    private final List<Employee> employeeList;

    public PurchaseOrderDataFactory(ApiMasterDataProvider apiProvider) {
        this.apiProvider   = apiProvider;
        this.vendorList    = safeList(apiProvider.getVendors());
        this.vendorNames   = vendorList.stream()
                .map(Vendor::getVendorName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        this.branchNames   = safeList(apiProvider.getBranches());
        this.currencyCodes = safeList(apiProvider.getCurrencies());
        this.termTemplates = safeList(apiProvider.getTermsAndConditions());
        this.productList   = safeList(apiProvider.getProducts());
        this.taxList       = safeList(apiProvider.getAllTaxObjects());
        this.employeeList  = safeList(apiProvider.getEmployees());
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private <T> T randomFrom(List<T> list) {
        return list.isEmpty() ? null : list.get(random.nextInt(list.size()));
    }

    private static LocalDate randomDateBetween(int startDays, int endDays) {
        long start = LocalDate.now().plusDays(startDays).toEpochDay();
        long end   = LocalDate.now().plusDays(endDays).toEpochDay();
        long day   = ThreadLocalRandom.current().nextLong(start, end + 1);
        return LocalDate.ofEpochDay(day);
    }

    private String randomEmployeeName() {
        if (employeeList.isEmpty()) return faker.name().firstName();
        Employee emp = randomFrom(employeeList);
        if (emp != null && emp.getUserName() != null) {
            String u = emp.getUserName();
            return u.contains("/") ? u.split("/")[1].trim() : u.trim();
        }
        return faker.name().firstName();
    }

    public PurchaseOrderData create(boolean isRenewal) {
        // 1) Dates
        LocalDate poDate       = LocalDate.now();
        LocalDate expectedDate = randomDateBetween(3, 15);
        LocalDate renewalDate  = isRenewal ? randomDateBetween(30, 90) : null;
        String   frequency     = isRenewal
                ? faker.options().option("Monthly", "Quarterly", "Half Yearly", "Yearly")
                : null;

        // 2) Vendor & segment
        Vendor chosenVendor   = randomFrom(vendorList);
        String vendorName     = chosenVendor != null ? chosenVendor.getVendorName() : null;
        String vendorSegment  = chosenVendor != null ? chosenVendor.getTaxsegment_name() : null;

        // 3) Taxes matching that segment
        List<Tax> matchingTaxes = taxList.stream()
                .filter(t -> t.getTaxsegment_name() != null
                        && t.getTaxsegment_name().equalsIgnoreCase(vendorSegment))
                .collect(Collectors.toList());

        // 4) Valid prefixes for products
        Set<String> validPrefixes = matchingTaxes.stream()
                .map(Tax::getTaxPrefix)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());

        // 4.5) Exclude service products entirely
        List<Product> nonServiceProducts = productList.stream()
                .filter(p -> {
                    String t = p.getProductTypeName();
                    return t == null || !t.equalsIgnoreCase("Service");
                })
                .collect(Collectors.toList());

        // 5) Filter products by tax-prefix, fallback to non-service products
        List<Product> filteredProducts = nonServiceProducts.stream()
                .filter(p -> p.getTax() != null && validPrefixes.contains(p.getTax().trim()))
                .collect(Collectors.toList());
        if (filteredProducts.isEmpty()) {
            filteredProducts = new ArrayList<>(nonServiceProducts);
        }

        // 6) Build 1–4 line items
        int count = faker.number().numberBetween(1, 4);
        List<LineItem> lineItems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Product product = randomFrom(filteredProducts);
            if (product == null) continue;

            int quantity = faker.number().numberBetween(1, 25);
            BigDecimal masterPrice;
            try {
                masterPrice = new BigDecimal(product.getProductPrice());
            } catch (Exception ex) {
                masterPrice = BigDecimal.ZERO;
            }
            BigDecimal price = masterPrice.compareTo(BigDecimal.ZERO) > 0
                    ? masterPrice
                    : BigDecimal.valueOf(faker.number().randomDouble(2, 500, 5000))
                    .setScale(2, RoundingMode.HALF_UP);

            // discount percentage forced between 0–15%
            BigDecimal discountPct = BigDecimal.valueOf(random.nextDouble() * 15)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal discountAmt = price
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(discountPct)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            Tax tax = randomFrom(matchingTaxes);
            BigDecimal taxRate = BigDecimal.ZERO;
            String    taxPrefix = "GST 0%";
            if (tax != null && tax.getPercentage() != null) {
                try {
                    taxRate = new BigDecimal(tax.getPercentage())
                            .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                    taxPrefix = tax.getPercentage().replaceAll("[^\\d.-]", "") + "%";
                } catch (NumberFormatException ex) {
                    // fallback
                }
            }

            LineItem item = LineItem.builder()
                    .productGroup(product.getProductGroupName())
                    .productCode(product.getProductCode())
                    .productName(product.getProductName())
                    .description(product.getProductDesc())
                    .quantity(quantity)
                    .price(price)
                    .discountAmt(discountAmt)
                    .taxPrefix(taxPrefix)
                    .taxRate(taxRate)
                    .build();
            item.computeTotal();
            lineItems.add(item);
        }

        // 7) Realistic Indian mobile: starts 6–9 + 9 digits
        String mobi = faker.regexify("[6-9]\\d{9}");

        // 8) Timestamped cover note
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStamp      = LocalDateTime.now().format(dtf);
        String coverNote = String.format(
                "PO generated on %s — please inspect goods upon arrival.",
                nowStamp
        );

        // 9) Assemble PurchaseOrderData
        PurchaseOrderData data = PurchaseOrderData.builder()
                .branchName(randomFrom(branchNames))
                .poRefNo("PO-" + faker.number().digits(6))
                .poDate(poDate)
                .expectedDate(expectedDate)
                .vendorName(vendorName)
                .billTo(faker.address().fullAddress())
                .shipTo(faker.address().fullAddress())
                .requestedBy(randomEmployeeName())
                .requestorContactDetails(mobi)
                .deliveryTerms("Deliver within 7 days")
                .paymentTerms("Net 30 days")
                .dispatchMode(randomFrom(dispatchModes))
                .currency(randomFrom(currencyCodes))
                .exchangeRate(BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)))
                .coverNote(coverNote)
                .renewal(isRenewal)
                .renewalDate(renewalDate)
                .frequency(frequency)
                .lineItems(lineItems)
                .addOnCharges(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 500)))
                .additionalDiscount(BigDecimal.valueOf(random.nextDouble() * 15).setScale(2, RoundingMode.HALF_UP))
                .freightCharges(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 300)))
                .additionalTax(matchingTaxes.isEmpty()
                        ? "0%"
                        : matchingTaxes.get(random.nextInt(matchingTaxes.size()))
                        .getPercentage().replaceAll("[^\\d.-]", "") + "%")
                .roundOff(BigDecimal.ZERO)
                .termsAndConditions(randomFrom(termTemplates))
                .termsEditorText("Ensure packaging standards are met.")
                .build();

        data.computeNetAmount();
        data.computeGrandTotal();

        return data;
    }
}