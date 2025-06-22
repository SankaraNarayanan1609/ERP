package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput;
import com.Vcidex.StoryboardSystems.Purchase.POJO.*;
import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Factory class to create randomized, valid PurchaseOrderData objects
 * used for automation testing of Direct Purchase Order flow.
 */
public class PurchaseOrderDataFactory {

    private final ApiMasterDataProvider apiProvider;
    private final Faker faker = new Faker();
    private final Random random = new Random();
    private final List<String> branchNames;
    private final List<Vendor> vendorList;
    private final List<String> currencyCodes;
    private final List<String> termTemplates;
    private final List<Product> productList;
    private final List<Tax> taxList;
    private final List<Employee> employeeList;

    private final List<String> dispatchModes = Arrays.asList("Courier", "Hand Delivery", "Email Copy");

    public PurchaseOrderDataFactory(ApiMasterDataProvider apiProvider) {
        this.apiProvider    = apiProvider;
        this.vendorList     = safeList(apiProvider.getVendors());
        this.branchNames    = safeList(apiProvider.getBranches());
        this.currencyCodes  = safeList(apiProvider.getCurrencies());
        this.termTemplates  = safeList(apiProvider.getTermsAndConditions());
        this.productList    = safeList(apiProvider.getProducts());
        this.taxList        = safeList(apiProvider.getAllTaxObjects());
        this.employeeList   = safeList(apiProvider.getEmployees());
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

    /**
     * Build a random PO, picking line-items exactly of the requested type (SERVICE vs PHYSICAL).
     */
    public PurchaseOrderData create(PurchaseTestInput input) {
        ProductType desiredType = input.getProductType();

        // 1) dates
        LocalDate poDate       = LocalDate.now();
        LocalDate expectedDate = randomDateBetween(3, 15);

        // 2) pick vendor + segment
        Vendor chosenVendor    = randomFrom(vendorList);
        String vendorName      = chosenVendor != null ? chosenVendor.getVendorName()       : null;
        String vendorSegment   = chosenVendor != null ? chosenVendor.getTaxsegment_name() : null;

        // 3) load matching-tax
        List<Tax> matchingTaxes = taxList.stream()
                .filter(t -> t.getTaxsegment_name() != null
                        && t.getTaxsegment_name().equalsIgnoreCase(vendorSegment))
                .collect(Collectors.toList());
        Set<String> validPrefixes = matchingTaxes.stream()
                .map(Tax::getTaxPrefix)
                .filter(Objects::nonNull)
                .map(String::trim)
                .collect(Collectors.toSet());

        // 4) TYPE-AWARE product selection
        // new version – require a real, non-null type for physical products:
        List<Product> byType = productList.stream()
                .filter(p -> {
                    ProductType pt = p.getProductType();
                    if (pt == null) return false;                     // drop nulls immediately
                    if (desiredType == ProductType.SERVICE) {
                        return pt == ProductType.SERVICE;
                    } else {
                        return pt != ProductType.SERVICE;
                    }
                })
                .collect(Collectors.toList());

        // 5) narrow by tax
        List<Product> filteredProducts = byType.stream()
                .filter(p -> p.getTax() != null
                        && validPrefixes.contains(p.getTax().trim()))
                .collect(Collectors.toList());

        // 6) fallback to same-type bucket
        if (filteredProducts.isEmpty()) {
            System.out.println("⚠️ No products matched tax criteria → falling back to same-type bucket.");
            filteredProducts = new ArrayList<>(byType);
        }
        if (filteredProducts.isEmpty()) {
            throw new RuntimeException("❌ No valid products of type "
                    + input.getProductType() + " available! Check master data.");
        }

        // 7) build up to itemCount distinct line-items
        int desiredCount = faker.number().numberBetween(1, 6);     // or 1–4, whatever you want
        List<Product> pool = new ArrayList<>(filteredProducts);

        // if the pool is empty, either fallback or throw
        if (pool.isEmpty()) {
            throw new RuntimeException("❌ No products of type " + input.getProductType() + " available!");
        }

        // shuffle & pick at most desiredCount distinct products
        Collections.shuffle(pool);
        int pickCount = Math.min(desiredCount, pool.size());
        List<Product> picks = pool.subList(0, pickCount);

        List<LineItem> lineItems = new ArrayList<>();
        for (Product product : picks) {
            int quantity = faker.number().numberBetween(1, 25);
            BigDecimal masterPrice;
            try { masterPrice = new BigDecimal(product.getProductPrice()); } // Cannot resolve method 'getProductPrice' in 'Product'
            catch (Exception ex) { masterPrice = BigDecimal.ZERO; }

            BigDecimal price = masterPrice.compareTo(BigDecimal.ZERO) > 0
                    ? masterPrice
                    : BigDecimal.valueOf(faker.number().randomDouble(2, 500, 5000))
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal discountPct = BigDecimal.valueOf(random.nextDouble() * 15)
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal discountAmt = price
                    .multiply(BigDecimal.valueOf(quantity))
                    .multiply(discountPct)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            Tax tax = randomFrom(matchingTaxes);
            BigDecimal taxRate = BigDecimal.ZERO;
            String taxPrefix   = "GST 0%";
            if (tax != null && tax.getPercentage() != null) {
                try {
                    taxRate   = new BigDecimal(tax.getPercentage())
                            .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                    taxPrefix = tax.getPercentage().replaceAll("[^\\d.-]", "") + "%";
                } catch (NumberFormatException ignored) {}
            }

            LineItem item = LineItem.builder()
                    .productGroup(product.getProductGroupName()) // Cannot resolve method 'getProductGroupName' in 'Product'
                    .productCode (product.getProductCode()) // Cannot resolve method 'getProductCode' in 'Product'
                    .productName (product.getProductName()) // Cannot resolve method 'getProductName' in 'Product'
                    .description (product.getProductDesc()) // Cannot resolve method 'getProductDesc' in 'Product'
                    .quantity    (quantity)
                    .price       (price)
                    .discountAmt (discountAmt)
                    .taxPrefix   (taxPrefix)
                    .taxRate     (taxRate)
                    .product     (product)
                    .build();
            item.computeTotal();
            lineItems.add(item);
        }

        // 8) finalize header
        String mobile    = faker.regexify("[6-9]\\d{9}");
        String nowTs     = LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String coverNote = "PO generated on " + nowTs
                + " — please inspect goods upon arrival.";

        PurchaseOrderData data = PurchaseOrderData.builder()
                .branchName             (randomFrom(branchNames))
                .poRefNo                ("PO-" + faker.number().digits(6))
                .poDate                 (poDate)
                .expectedDate           (expectedDate)
                .vendorName             (vendorName)
                .billTo                 (faker.address().fullAddress())
                .shipTo                 (faker.address().fullAddress())
                .requestedBy            (randomEmployeeName())
                .requestorContactDetails(mobile)
                .deliveryTerms          ("Deliver within 7 days")
                .paymentTerms           ("Net 30 days")
                .dispatchMode           (randomFrom(dispatchModes))
                .currency               (randomFrom(currencyCodes))
                .exchangeRate           (BigDecimal.valueOf(
                        faker.number().randomDouble(2, 1, 100)))
                .coverNote              (coverNote)
                .renewal                (false)  // you can wire this from input if desired
                .renewalDate            (null)
                .frequency              (null)
                .lineItems              (lineItems)
                .addOnCharges           (BigDecimal.valueOf(
                        faker.number().randomDouble(2, 100, 500)))
                .additionalDiscount     (BigDecimal.valueOf(
                                random.nextDouble() * 15)
                        .setScale(2, RoundingMode.HALF_UP))
                .freightCharges         (BigDecimal.valueOf(
                        faker.number().randomDouble(2, 100, 300)))
                .additionalTax          (matchingTaxes.isEmpty()
                        ? "0%"
                        : matchingTaxes.get(
                                random.nextInt(matchingTaxes.size()))
                        .getPercentage()
                        .replaceAll("[^\\d.-]", "") + "%")
                .roundOff               (BigDecimal.ZERO)
                .termsAndConditions     (randomFrom(termTemplates))
                .termsEditorText        ("Ensure packaging standards are met.")
                .build();

        data.computeNetAmount();
        data.computeGrandTotal();

        // tag it with the requested product type
        data.setProductType(input.getProductType());
        return data;
    }

    /**
     * Now correctly calls create(input), so your service vs physical
     * filter actually gets honored.
     */
    public PurchaseOrderData generateDataFor(PurchaseTestInput input) {

        PurchaseOrderData poData = create(input);
        poData.setCurrencyCode(input.getCurrency());
        System.out.println("📌 EntryType for test: " + input.getEntryType());
        return poData;
    }
}