// File: src/main/java/com/Vcidex/StoryboardSystems/Utils/DataFactory/PurchaseOrderDataFactory.java
package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.POJO.*;
import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class PurchaseOrderDataFactory {

    private final ApiMasterDataProvider apiProvider;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    // ──────────────────────────────────────────────────────────────────────────
    // Master data lists
    // ──────────────────────────────────────────────────────────────────────────
    private final List<String>   branchNames;
    private final List<Vendor>   vendorList;     // Keep full Vendor POJOs
    private final List<String>   vendorNames;
    private final List<String>   currencyCodes;
    private final List<String>   termTemplates;
    private final List<String>   dispatchModes = Arrays.asList("Courier", "Hand Delivery", "Email Copy");
    private final List<Product>  productList;
    private final List<Tax>      taxList;
    private final List<Employee> employeeList;

    public PurchaseOrderDataFactory(ApiMasterDataProvider apiProvider) {
        this.apiProvider = apiProvider;

        // ── Fetch full Vendor POJOs ──
        List<Vendor> fetchedVendors = apiProvider.getVendors();
        this.vendorList = safeList(fetchedVendors);

        // Also extract just the vendor‐name strings for dropdowns
        this.vendorNames = vendorList.stream()
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

    // Safely return non-null lists
    private <T> List<T> safeList(List<T> list) {
        return (list == null) ? Collections.emptyList() : list;
    }

    // Utility for random selection from list
    private <T> T randomFrom(List<T> list) {
        return (list.isEmpty() ? null : list.get(random.nextInt(list.size())));
    }

    /** Picks a random LocalDate between now+startDays and now+endDays. */
    private static LocalDate randomDateBetween(int startDaysFromNow, int endDaysFromNow) {
        long start = LocalDate.now().plusDays(startDaysFromNow).toEpochDay();
        long end   = LocalDate.now().plusDays(endDaysFromNow).toEpochDay();
        long day   = ThreadLocalRandom.current().nextLong(start, end + 1);
        return LocalDate.ofEpochDay(day);
    }

    /** Random employee name from employeeList, fallback to Faker if unavailable. */
    private String randomEmployeeName() {
        if (employeeList.isEmpty()) {
            return faker.name().firstName();
        }
        Employee emp = randomFrom(employeeList);
        if (emp != null && emp.getUserName() != null) {
            String fullName = emp.getUserName();
            if (fullName.contains("/")) {
                return fullName.split("/")[1].trim();
            } else {
                return fullName.trim();
            }
        }
        return faker.name().firstName();
    }

    /**
     * Generate a random LineItem by picking a random product list entry and a matching Tax.
     * (This method is no longer used directly; see create(...) below for segment‐aware logic.)
     */
    @SuppressWarnings("unused")
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
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        Tax randomTax = randomFrom(taxList);
        String taxPrefix = (randomTax != null && randomTax.getTaxPrefix() != null)
                ? randomTax.getTaxPrefix() : "GST 18%";
        BigDecimal taxRate = (randomTax != null && randomTax.getPercentage() != null)
                ? new BigDecimal(randomTax.getPercentage()).divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                : BigDecimal.valueOf(0.18);

        LineItem item = LineItem.builder()
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
                .build();

        item.computeTotal();
        return item;
    }

    /**
     * Main method to generate random PurchaseOrderData using dynamic master data,
     * but now filtered so that each product matches the chosen vendor’s tax segment.
     */
    public PurchaseOrderData create(boolean isRenewal) {
        // ────────────── STEP 1: Basic dates ──────────────
        LocalDate poDate       = LocalDate.now();
        LocalDate expectedDate = randomDateBetween(3, 15);
        LocalDate renewalDate  = isRenewal ? randomDateBetween(30, 90) : null;
        String   frequency     = isRenewal
                ? faker.options().option("Monthly", "Quarterly", "Half Yearly", "Yearly")
                : null;

        // ───────── STEP 2: Pick a random Vendor ─────────
        Vendor chosenVendor = randomFrom(vendorList);
        String vendorName   = (chosenVendor != null)
                ? chosenVendor.getVendorName()
                : null;

        // ───────── STEP 3: Get that vendor’s tax segment ─────────
        // e.g. "Within State", "Interstate", "Overseas"
        String vendorSegment = (chosenVendor != null)
                ? chosenVendor.getTaxsegment_name()
                : null;

        // ───────── STEP 4: Build a set of validTaxPrefixes ─────────
        // i.e. all Tax.getTaxPrefix() where Tax.getTaxsegment_name().equalsIgnoreCase(vendorSegment)
        Set<String> validTaxPrefixes = taxList.stream()
                .filter(t -> {
                    String seg = t.getTaxsegment_name();
                    return seg != null
                            && vendorSegment != null
                            && seg.equalsIgnoreCase(vendorSegment);
                })
                .map(Tax::getTaxPrefix)    // e.g. "GST 18%"
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());

        // If no matching prefixes found, validTaxPrefixes will be empty.
        // ──────────────────────────────────────────────────────────────────────────

        // ───────── STEP 4A: Re‐create matchingTaxes from that same vendor segment ─────────
        List<Tax> matchingTaxes = taxList.stream()
                .filter(t -> {
                    String seg = t.getTaxsegment_name();
                    return seg != null
                            && vendorSegment != null
                            && seg.equalsIgnoreCase(vendorSegment);
                })
                .collect(Collectors.toList());
        // ──────────────────────────────────────────────────────────────────────────

        // ───────── STEP 5: Filter products whose product.getTax() ∈ validTaxPrefixes ─────────
        List<Product> filteredProducts = productList.stream()
                .filter(p -> {
                    String prodTax = p.getTax();  // must be exactly "GST xx%"
                    return prodTax != null
                            && validTaxPrefixes.contains(prodTax.trim());
                })
                .collect(Collectors.toList());

        // Fall back to all products if none matched:
        if (filteredProducts.isEmpty()) {
            filteredProducts = productList;
        }

        // ────────────── STEP 6: Build random line items from filteredProducts ──────────────
        int lineItemCount = faker.number().numberBetween(1, 4);
        List<LineItem> lineItems = new ArrayList<>();

        for (int i = 0; i < lineItemCount; i++) {
            Product product = randomFrom(filteredProducts);
            if (product == null) continue;

            // 1) Quantity & Price
            int quantity;
            try {
                quantity = faker.number().numberBetween(1, 25);
            } catch (Exception e) {
                quantity = 1;
            }
            // If the master “product price” is zero or null, fall back to a random value:
            BigDecimal masterPrice = BigDecimal.ZERO;
            try {
                masterPrice = new BigDecimal(product.getProductPrice());
            } catch (Exception ignored) { }

            BigDecimal price;
            if (masterPrice.compareTo(BigDecimal.ZERO) <= 0) {
                // generate a random price between, say, 500 and 5000:
                double randomVal = faker.number().randomDouble(2, 500, 5000);
                price = BigDecimal.valueOf(randomVal).setScale(2, RoundingMode.HALF_UP);
            } else {
                price = masterPrice;
            }

            // 2) Discount % up to 15%
            BigDecimal discountPct = BigDecimal.valueOf(
                    faker.number().randomDouble(2, 0, 15)
            );
            // 3) Discount amount = (price * quantity) * (discountPct/100)
            BigDecimal discountAmt = price
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(discountPct)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // 4) Pick a Tax POJO from matchingTaxes (if any)
            Tax chosenTax   = randomFrom(matchingTaxes);
            BigDecimal taxRate = BigDecimal.ZERO;
            String    taxPrefix = "GST 0%";

            if (chosenTax != null && chosenTax.getPercentage() != null) {
                try {
                    // chosenTax.getPercentage() is something like "18" or "5"
                    taxRate   = new BigDecimal(chosenTax.getPercentage())
                            .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                    // and taxPrefix becomes e.g. "18%"
                    taxPrefix = chosenTax.getPercentage().replaceAll("[^\\d.\\-]", "") + "%";
                } catch (NumberFormatException e) {
                    taxRate   = BigDecimal.ZERO;
                    taxPrefix = "GST 0%";
                }
            }

            // 5) Construct the LineItem
            LineItem item = LineItem.builder()
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
                    .build();
            item.computeTotal();
            lineItems.add(item);
        }

        // ────────────── STEP 7: Build PurchaseOrderData via builder ──────────────
        PurchaseOrderData data = PurchaseOrderData.builder()
                .branchName(randomFrom(branchNames))
                .poRefNo("PO-" + faker.number().digits(6))
                .poDate(poDate)                          // <— add this line!
                .expectedDate(expectedDate)
                .vendorName(vendorName)
                .billTo(faker.address().fullAddress())
                .shipTo(faker.address().fullAddress())
                .requestedBy(randomEmployeeName())
                .requestorContactDetails(faker.phoneNumber().phoneNumber())
                .deliveryTerms("Deliver within 7 days")
                .paymentTerms("Net 30 days")
                .dispatchMode(randomFrom(dispatchModes))
                .currency(randomFrom(currencyCodes))
                .coverNote("Please ensure quality check before dispatch.")
                .renewal(isRenewal)
                .renewalDate(renewalDate)
                .frequency(frequency)
                .lineItems(lineItems)
                .addOnCharges(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 500)))
                .additionalDiscount(BigDecimal.valueOf(faker.number().randomDouble(2, 5, 20)))
                .freightCharges(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 300)))
                // Footer “additionalTax” expects something like "18%" or "5%"
                .additionalTax(randomFrom(matchingTaxes) != null
                        ? randomFrom(matchingTaxes).getPercentage().replaceAll("[^\\d.\\-]", "") + "%"
                        : "0%")
                .roundOff(BigDecimal.ZERO)
                .termsAndConditions(randomFrom(termTemplates))
                .termsEditorText("Ensure packaging standards are met.")
                .build();

        data.computeNetAmount();
        data.computeGrandTotal();

        // (Optional) debug print
        System.out.println("[DEBUG] Generated PurchaseOrderData: " + data);
        return data;
    }
}