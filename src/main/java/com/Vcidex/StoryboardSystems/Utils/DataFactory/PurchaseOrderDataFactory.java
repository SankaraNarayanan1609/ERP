package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.POJO.*;
import com.github.javafaker.Faker; // Faker is used to generate realistic fake test data
import lombok.Builder;             // Lombok: Enables @Builder pattern in POJO creation
import lombok.Singular;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom; // Used for generating random date between a range
import java.util.stream.Collectors;

/**
 * Factory class to generate randomized test data for Direct Purchase Orders.
 * This uses master data from API responses and fills random values using Java Faker.
 */
public class PurchaseOrderDataFactory {

    // ─── Dependencies ─────────────────────────────────────────────
    private final ApiMasterDataProvider apiProvider; // Supplies master data via API
    private final Faker faker = new Faker();         // Generates dummy but realistic data
    private final Random random = new Random();      // Fallback randomizer

    // ─── Cached Master Lists ──────────────────────────────────────
    private final List<String>   branchNames;
    private final List<Vendor>   vendorList;
    private final List<String>   vendorNames;
    private final List<String>   currencyCodes;
    private final List<String>   termTemplates;
    private final List<String>   dispatchModes = Arrays.asList("Courier", "Hand Delivery", "Email Copy");
    private final List<Product>  productList;
    private final List<Tax>      taxList;
    private final List<Employee> employeeList;

    /**
     * Initializes factory with data from API.
     * Preloads master lists and ensures null safety.
     */
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

    /**
     * Safely returns a list or empty list if null
     */
    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * Picks one random element from a list
     */
    private <T> T randomFrom(List<T> list) {
        return list.isEmpty() ? null : list.get(random.nextInt(list.size()));
    }

    /**
     * Generates a random date between N days from today.
     */
    private static LocalDate randomDateBetween(int startDays, int endDays) {
        long start = LocalDate.now().plusDays(startDays).toEpochDay();
        long end   = LocalDate.now().plusDays(endDays).toEpochDay();
        long day   = ThreadLocalRandom.current().nextLong(start, end + 1);
        return LocalDate.ofEpochDay(day);
    }

    /**
     * Selects a random employee and returns only the username (after slash if exists).
     */
    private String randomEmployeeName() {
        if (employeeList.isEmpty()) return faker.name().firstName();
        Employee emp = randomFrom(employeeList);
        if (emp != null && emp.getUserName() != null) {
            String u = emp.getUserName();
            return u.contains("/") ? u.split("/")[1].trim() : u.trim();
        }
        return faker.name().firstName();
    }

    /**
     * Generates a fully populated PurchaseOrderData object using master data and randomized fields.
     * @param isRenewal Whether to include renewal-specific data
     * @return PurchaseOrderData for testing a Direct PO flow
     */
    public PurchaseOrderData create(boolean isRenewal) {
        // ── 1. Dates ─────────────────────────────────────────────
        LocalDate poDate       = LocalDate.now();
        LocalDate expectedDate = randomDateBetween(3, 15);
        LocalDate renewalDate  = isRenewal ? randomDateBetween(30, 90) : null;
        String   frequency     = isRenewal
                ? faker.options().option("Monthly", "Quarterly", "Half Yearly", "Yearly")
                : null;

        // ── 2. Vendor Selection ─────────────────────────────────
        Vendor chosenVendor   = randomFrom(vendorList);
        String vendorName     = chosenVendor != null ? chosenVendor.getVendorName() : null;
        String vendorSegment  = chosenVendor != null ? chosenVendor.getTaxsegment_name() : null;

        // ── 3. Tax Mapping for Segment ───────────────────────────
        List<Tax> matchingTaxes = taxList.stream()
                .filter(t -> t.getTaxsegment_name() != null
                        && t.getTaxsegment_name().equalsIgnoreCase(vendorSegment))
                .collect(Collectors.toList());

        // ── 4. Get All Tax Prefixes ──────────────────────────────
        Set<String> validPrefixes = matchingTaxes.stream()
                .map(Tax::getTaxPrefix)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());

        // ── 4.5. Exclude Service-Type Products ──────────────────
        List<Product> nonServiceProducts = productList.stream()
                .filter(p -> {
                    String t = p.getProductTypeName();
                    return t == null || !t.equalsIgnoreCase("Service");
                })
                .collect(Collectors.toList());

        // ── 5. Filter Products Based on Tax Prefix ───────────────
        List<Product> filteredProducts = nonServiceProducts.stream()
                .filter(p -> p.getTax() != null && validPrefixes.contains(p.getTax().trim()))
                .collect(Collectors.toList());
        if (filteredProducts.isEmpty()) {
            filteredProducts = new ArrayList<>(nonServiceProducts); // fallback
        }

        // 6) Create 1–4 random line items for the PO
        int count = faker.number().numberBetween(1, 4); // Number of line items (1 to 3)
        List<LineItem> lineItems = new ArrayList<>();

        for (int i = 0; i < count; i++) {

            // Pick a random product from the filtered list (based on tax-prefix and type)
            Product product = randomFrom(filteredProducts);
            if (product == null) continue; // Skip if product is null

            // Generate a random quantity between 1 and 25
            int quantity = faker.number().numberBetween(1, 25);

            // Attempt to get the product's master price from the API
            BigDecimal masterPrice;
            try {
                masterPrice = new BigDecimal(product.getProductPrice());
            } catch (Exception ex) {
                masterPrice = BigDecimal.ZERO; // Fallback to 0 if parsing fails
            }

            // If master price is valid, use it; else generate a fake price between 500–5000
            BigDecimal price = masterPrice.compareTo(BigDecimal.ZERO) > 0
                    ? masterPrice
                    : BigDecimal.valueOf(faker.number().randomDouble(2, 500, 5000))
                    .setScale(2, RoundingMode.HALF_UP);

            // Random discount % between 0 and 15%
            BigDecimal discountPct = BigDecimal.valueOf(random.nextDouble() * 15)
                    .setScale(2, RoundingMode.HALF_UP);

            // Calculate discount amount = (price × qty × discount %)
            BigDecimal discountAmt = price
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(discountPct)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Pick a matching tax object based on vendor’s tax segment
            Tax tax = randomFrom(matchingTaxes);
            BigDecimal taxRate = BigDecimal.ZERO;
            String taxPrefix = "GST 0%"; // Default fallback tax label

            if (tax != null && tax.getPercentage() != null) {
                try {
                    taxRate = new BigDecimal(tax.getPercentage())
                            .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP); // e.g., 18% → 0.18
                    taxPrefix = tax.getPercentage().replaceAll("[^\\d.-]", "") + "%"; // e.g., "GST 18%" → "18%"
                } catch (NumberFormatException ex) {
                    // Use fallback values if parsing fails
                }
            }

            // Build LineItem using Lombok Builder pattern
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
                    .product(product) // Attach the full product object for future reference
                    .build();

            item.computeTotal();  // Calculate total = (qty * price - discount + tax)
            lineItems.add(item);  // Add to PO list
        }

        // ── 7. Generate Random Contact ──────────────────────────
        String mobi = faker.regexify("[6-9]\\d{9}");

        // ── 8. Cover Note with Timestamp ─────────────────────────
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowStamp       = LocalDateTime.now().format(dtf);
        String coverNote = String.format(
                "PO generated on %s — please inspect goods upon arrival.",
                nowStamp
        );

        // ── 9. Final Assembly ────────────────────────────────────
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

        // ── 10. Final Calculations ───────────────────────────────
        data.computeNetAmount();
        data.computeGrandTotal();

        return data;
    }
}