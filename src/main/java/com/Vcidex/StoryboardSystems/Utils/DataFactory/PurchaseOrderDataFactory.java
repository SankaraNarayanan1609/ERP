/**
 * PurchaseOrderDataFactory is responsible for generating randomized but logically valid
 * {@link PurchaseOrderData} objects used for test automation in Direct Purchase Order (PO) flows.
 *
 * 🔧 Core Responsibilities:
 *   - Fetch and cache master data from API (e.g., vendors, taxes, products)
 *   - Filter product-tax combinations based on vendor tax segment
 *   - Generate 1–4 random line items with realistic price, tax, discount
 *   - Assemble a complete PO object, optionally with renewal data
 *
 * 💡 Usage:
 *   This factory is called during test execution (e.g., in DirectPOTest) to create fresh
 *   purchase order data that mimics realistic ERP inputs.
 */

package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput;
import com.Vcidex.StoryboardSystems.Purchase.POJO.*;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput;
import com.github.javafaker.Faker;
import lombok.Builder;
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

    // ────────────────────────────────
    // SECTION: Dependencies & Faker
    // ────────────────────────────────
    private final ApiMasterDataProvider apiProvider;
    private final Faker faker = new Faker();
    private final Random random = new Random();

    // ────────────────────────────────
    // SECTION: Master Data Cache
    // ────────────────────────────────
    private final List<String>   branchNames;
    private final List<Vendor>   vendorList;
    private final List<String>   vendorNames;
    private final List<String>   currencyCodes;
    private final List<String>   termTemplates;
    private final List<Product>  productList;
    private final List<Tax>      taxList;
    private final List<Employee> employeeList;

    private final List<String> dispatchModes = Arrays.asList("Courier", "Hand Delivery", "Email Copy");

    /**
     * Constructor pulls all required master data once and caches it.
     * This ensures every PO created uses valid business-specific data.
     */
    public PurchaseOrderDataFactory(ApiMasterDataProvider apiProvider) {
        this.apiProvider = apiProvider;

        // Ensure base URL is not null or empty before proceeding
        if (apiProvider == null || apiProvider.getBaseUrl() == null || apiProvider.getBaseUrl().isEmpty()) { // Cannot resolve method 'getBaseUrl' in 'ApiMasterDataProvider'
            throw new IllegalArgumentException("Base URL for API is missing or invalid.");
        }

        // Ensure token is valid
        if (apiProvider.getAuthToken() == null || apiProvider.getAuthToken().isEmpty()) { // Cannot resolve method 'getAuthToken' in 'ApiMasterDataProvider'
            throw new IllegalArgumentException("Auth token is missing or invalid.");
        }

        // Pull vendor objects and cache both objects and names
        this.vendorList = safeList(apiProvider.getVendors());
        this.vendorNames = vendorList.stream()
                .map(Vendor::getVendorName)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // Load all other master data used in PO creation
        this.branchNames = safeList(apiProvider.getBranches());
        this.currencyCodes = safeList(apiProvider.getCurrencies());
        this.termTemplates = safeList(apiProvider.getTermsAndConditions());
        this.productList = safeList(apiProvider.getProducts());
        this.taxList = safeList(apiProvider.getAllTaxObjects());
        this.employeeList = safeList(apiProvider.getEmployees());
    }

    /**
     * Utility method to return a safe (non-null) list to avoid NPEs.
     */
    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    /**
     * Utility to select a random element from a list, or null if empty.
     */
    private <T> T randomFrom(List<T> list) {
        return list.isEmpty() ? null : list.get(random.nextInt(list.size()));
    }

    /**
     * Generates a date between [today + startDays] and [today + endDays].
     * Useful for expected delivery dates or renewal dates.
     */
    private static LocalDate randomDateBetween(int startDays, int endDays) {
        long start = LocalDate.now().plusDays(startDays).toEpochDay();
        long end   = LocalDate.now().plusDays(endDays).toEpochDay();
        long day   = ThreadLocalRandom.current().nextLong(start, end + 1);
        return LocalDate.ofEpochDay(day);
    }

    /**
     * Extracts a username from a domain-prefixed string.
     * If employeeList is empty, falls back to Faker name.
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
     * Creates a full Purchase Order data object.
     *
     * @param isRenewal if true, includes renewal-related fields like renewalDate and frequency.
     * @return a fully-populated {@link PurchaseOrderData} object
     */
    public PurchaseOrderData create(boolean isRenewal) {

        // ─── Step 1: Dates & Renewal Settings ──────────────────────
        LocalDate poDate       = LocalDate.now();
        LocalDate expectedDate = randomDateBetween(3, 15);
        LocalDate renewalDate  = isRenewal ? randomDateBetween(30, 90) : null;
        String frequency       = isRenewal
                ? faker.options().option("Monthly", "Quarterly", "Half Yearly", "Yearly")
                : null;

        // ─── Step 2: Choose a vendor and resolve tax segment ──────
        Vendor chosenVendor  = randomFrom(vendorList);
        System.out.println("\n🟡 [DEBUG] Vendor Selection");
        System.out.println("   → Selected Vendor: " + (chosenVendor != null ? chosenVendor.getVendorName() : "❌ null"));

        String vendorName    = chosenVendor != null ? chosenVendor.getVendorName() : null;
        String vendorSegment = chosenVendor != null ? chosenVendor.getTaxsegment_name() : null;
        System.out.println("   → Vendor Segment: " + vendorSegment);

        // Match tax entries by vendor segment
        List<Tax> matchingTaxes = taxList.stream()
                .filter(t -> t.getTaxsegment_name() != null &&
                        t.getTaxsegment_name().equalsIgnoreCase(vendorSegment))
                .collect(Collectors.toList());

        // Get all valid tax prefixes from those taxes
        Set<String> validPrefixes = matchingTaxes.stream()
                .map(Tax::getTaxPrefix)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());

        // ─── Step 3: Filter valid products ─────────────────────────
        List<Product> nonServiceProducts = productList.stream()
                .filter(p -> {
                    String type = p.getProductTypeName();
                    return type == null || !type.equalsIgnoreCase("Service");
                }).collect(Collectors.toList());

        // Only pick products whose tax code matches valid prefixes
        List<Product> filteredProducts = nonServiceProducts.stream()
                .filter(p -> p.getTax() != null && validPrefixes.contains(p.getTax().trim()))
                .collect(Collectors.toList());

        // Fallback if no products matched tax criteria
        if (filteredProducts.isEmpty()) {
            System.out.println("⚠️ No products matched tax criteria. Falling back to non-service products.");
            filteredProducts = new ArrayList<>(nonServiceProducts);
        }

        // Final safety fallback if even non-service products are missing
        if (filteredProducts.isEmpty()) {
            throw new RuntimeException("❌ No valid products available for line item creation. Check master data.");
        }


        // ─── Step 4: Build Line Items ──────────────────────────────
        int itemCount = faker.number().numberBetween(1, 4);  // 1 to 3 line items
        List<LineItem> lineItems = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {

            System.out.println("\n🟡 [DEBUG] Product Filtering by Tax Prefix");
            System.out.println("   → Filtered Products: " + filteredProducts.size());
            filteredProducts.forEach(p -> System.out.println("     • " + p.getProductName() + " | Tax = " + p.getTax()));

            Product product = randomFrom(filteredProducts);
            if (product == null) continue;

            int quantity = faker.number().numberBetween(1, 25);

            // Parse product price or fallback to random
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

            BigDecimal discountPct = BigDecimal.valueOf(random.nextDouble() * 15)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal discountAmt = price.multiply(BigDecimal.valueOf(quantity))
                    .multiply(discountPct)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Assign tax
            Tax tax = randomFrom(matchingTaxes);

            System.out.println("\n🟡 [DEBUG] Tax Matching for Vendor Segment");
            System.out.println("   → Matching Taxes Found: " + matchingTaxes.size());
            matchingTaxes.forEach(t -> System.out.println("     • " + t.getTaxPrefix() + " (" + t.getPercentage() + ")"));



            BigDecimal taxRate = BigDecimal.ZERO;
            String taxPrefix = "GST 0%";

            if (tax != null && tax.getPercentage() != null) {
                try {
                    taxRate = new BigDecimal(tax.getPercentage())
                            .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                    taxPrefix = tax.getPercentage().replaceAll("[^\\d.-]", "") + "%";
                } catch (NumberFormatException ignored) {}
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
                    .product(product)
                    .build();

            item.computeTotal(); // Calculate line item total = (qty × price - discount + tax)
            lineItems.add(item);
        }

        // ✅ Add debug log here to inspect generated line items before assembly
        System.out.println("\n🟡 [DEBUG] Line Items to be Added = " + lineItems.size());
        lineItems.forEach(li ->
                System.out.println("     • " + li.getProductName() + " | Code = " + li.getProductCode() +
                        " | Qty = " + li.getQuantity() + " | Price = ₹" + li.getPrice())
        );

        // ─── Step 5: Additional Fields for Header ──────────────────
        String mobile       = faker.regexify("[6-9]\\d{9}");
        String nowTimestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String coverNote    = "PO generated on " + nowTimestamp + " — please inspect goods upon arrival.";

        // ─── Step 6: Final PO Assembly ─────────────────────────────
        PurchaseOrderData data = PurchaseOrderData.builder()
                .branchName(randomFrom(branchNames))
                .poRefNo("PO-" + faker.number().digits(6))
                .poDate(poDate)
                .expectedDate(expectedDate)
                .vendorName(vendorName)
                .billTo(faker.address().fullAddress())
                .shipTo(faker.address().fullAddress())
                .requestedBy(randomEmployeeName())
                .requestorContactDetails(mobile)
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
                .additionalDiscount(BigDecimal.valueOf(random.nextDouble() * 15)
                        .setScale(2, RoundingMode.HALF_UP))
                .freightCharges(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 300)))
                .additionalTax(matchingTaxes.isEmpty()
                        ? "0%"
                        : matchingTaxes.get(random.nextInt(matchingTaxes.size()))
                        .getPercentage()
                        .replaceAll("[^\\d.-]", "") + "%")
                .roundOff(BigDecimal.ZERO)
                .termsAndConditions(randomFrom(termTemplates))
                .termsEditorText("Ensure packaging standards are met.")
                .build();

        // 🔧 Debug log to inspect generated line items
        System.out.println("\n🟡 [DEBUG] Line Items to be Added = " + lineItems.size());
        lineItems.forEach(li ->
                System.out.println("     • " + li.getProductName() + " | Code = " + li.getProductCode() +
                        " | Qty = " + li.getQuantity() + " | Price = ₹" + li.getPrice())
        );

        // ─── Step 7: Final Calculations ────────────────────────────
        data.computeNetAmount();
        data.computeGrandTotal();

        System.out.println("\n🟢 [DEBUG] Header Assembly Complete:");
        System.out.println("   → Currency: " + data.getCurrency());
        System.out.println("   → Branch: " + data.getBranchName());
        System.out.println("   → Grand Total: ₹" + data.getGrandTotal());

        return data;
    }

    /**
     * Generates PurchaseOrderData using test input parameters (from T-way scenario).
     * @param input PurchaseTestInput instance containing product type, entry type, and currency
     * @return PurchaseOrderData object aligned to test input
     */
    public PurchaseOrderData generateDataFor(PurchaseTestInput input) {
        // Step 1: Create a normal PO with standard data
        PurchaseOrderData poData = create(false);

        // Step 2: Override dynamic fields based on scenario input
        poData.setProductType(input.getProductType());  // Already defined in POJO
        poData.setCurrencyCode(input.getCurrency());    // Already defined in POJO

        // Step 3: (Optional) Use entryType in logging or conditional logic
        System.out.println("📌 EntryType for test: " + input.getEntryType());

        return poData;
    }
}