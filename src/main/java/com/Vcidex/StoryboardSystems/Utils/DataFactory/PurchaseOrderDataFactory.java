package com.Vcidex.StoryboardSystems.Utils.DataFactory;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Tax;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput;
import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType; // MODEL enum
import com.Vcidex.StoryboardSystems.Utils.Logger.MasterLogger;
import com.Vcidex.StoryboardSystems.Utils.TaxService;
import com.github.javafaker.Faker;

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
    private final TaxService taxService;

    private final List<String> branchNames;
    private final List<Vendor> vendorList;
    private final List<String> currencyCodes;
    private final List<String> termTemplates;
    private final List<Product> productList;
    private final List<Tax> taxList;
    private final List<Employee> employeeList;

    private final List<String> dispatchModes = Arrays.asList("Courier", "Hand Delivery", "Email Copy");

    public PurchaseOrderDataFactory(ApiMasterDataProvider apiProvider) {
        this.apiProvider = apiProvider;
        this.vendorList = safeList(apiProvider.getVendors());
        this.branchNames = safeList(apiProvider.getBranches());
        this.currencyCodes = safeList(apiProvider.getCurrencies());
        this.termTemplates = safeList(apiProvider.getTermsAndConditions());
        this.productList = safeList(apiProvider.getProducts());
        this.taxList = safeList(apiProvider.getAllTaxObjects());
        this.employeeList = safeList(apiProvider.getEmployees());
        this.taxService = new TaxService(taxList);
    }

    private <T> List<T> safeList(List<T> list) {
        return list == null ? Collections.emptyList() : list;
    }

    private <T> T randomFrom(List<T> list) {
        return list.isEmpty() ? null : list.get(random.nextInt(list.size()));
    }

    private static LocalDate randomDateBetween(int startDays, int endDays) {
        long start = LocalDate.now().plusDays(startDays).toEpochDay();
        long end = LocalDate.now().plusDays(endDays).toEpochDay();
        long day = ThreadLocalRandom.current().nextLong(start, end + 1);
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

    public List<Product> filterByTaxSegment(
            List<Product> products,
            Set<String> validPrefixes,
            Set<String> validGids) {

        products.forEach(p ->
                MasterLogger.warn(String.format(
                        "▶️ Product '%s' has tax='%s', tax1='%s'; allowed prefixes=%s, GIDs=%s",
                        p.getProductName(), p.getTax(), p.getTax1(), validPrefixes, validGids))
        );

        return products.stream()
                .filter(p -> {
                    String t0 = p.getTax();
                    String t1 = p.getTax1();
                    boolean match0 = t0 != null && (validPrefixes.contains(t0.trim()) || validGids.contains(t0.trim()));
                    boolean match1 = t1 != null && (validPrefixes.contains(t1.trim()) || validGids.contains(t1.trim()));
                    return match0 || match1;
                })
                .collect(Collectors.toList());
    }

    /** Everything uses MODEL ProductType consistently. */
    public PurchaseOrderData generateDataFor(PurchaseTestInput input) {
        // --- Scenario knobs ---
        com.Vcidex.StoryboardSystems.Purchase.Model.ProductType desiredType = input.productType();
        boolean isFx = !"INR".equalsIgnoreCase(input.currency()); // FX? -> Overseas vendor

        LocalDate poDate = LocalDate.now();
        LocalDate expectedDate = randomDateBetween(3, 15);

        // --- Vendor pick: FX => Overseas; INR => Non-Overseas ---
        List<Vendor> vendorCandidates = vendorList.stream()
                .filter(v -> {
                    String seg = Optional.ofNullable(v.getTaxsegment_name()).orElse("").trim();
                    return isFx ? seg.equalsIgnoreCase("Overseas") : !seg.equalsIgnoreCase("Overseas");
                })
                .collect(Collectors.toList());
        if (vendorCandidates.isEmpty()) vendorCandidates = vendorList; // fallback if masters are thin

        Vendor chosenVendor = randomFrom(vendorCandidates);
        String vendorName = chosenVendor != null ? chosenVendor.getVendorName() : null;
        String vendorSegment = Optional.ofNullable(chosenVendor != null ? chosenVendor.getTaxsegment_name() : null)
                .orElse("").trim();

        // --- Taxes by vendor segment (as you had) ---
        List<Tax> matchingTaxes = taxList.stream()
                .filter(t -> vendorSegment.equalsIgnoreCase(
                        Optional.ofNullable(t.getTaxsegment_name()).orElse("").trim()))
                .collect(Collectors.toList());
        MasterLogger.warn("🔍 Vendor Tax Segment: '" + vendorSegment + "'");
        matchingTaxes.forEach(t -> MasterLogger.warn("🔎 Available Tax Segment: '" + t.getTaxsegment_name() + "'"));

        Set<String> validTaxPrefixes = matchingTaxes.stream()
                .map(Tax::getTaxPrefix).filter(Objects::nonNull).map(String::trim).collect(Collectors.toSet());
        Set<String> validTaxGids = matchingTaxes.stream()
                .map(Tax::getTaxGid).filter(Objects::nonNull).map(String::trim).collect(Collectors.toSet());

        // --- Product filtering by scenario type ---
        List<Product> byType = productList.stream()
                .filter(p -> {
                    com.Vcidex.StoryboardSystems.Purchase.Model.ProductType pt = p.getProductType(); // MODEL enum
                    if (pt == null) return false;
                    if (desiredType == com.Vcidex.StoryboardSystems.Purchase.Model.ProductType.SERVICE)
                        return pt == com.Vcidex.StoryboardSystems.Purchase.Model.ProductType.SERVICE;
                    // "all other than service"
                    return pt != com.Vcidex.StoryboardSystems.Purchase.Model.ProductType.SERVICE;
                })
                .collect(Collectors.toList());

        // --- Tax-segment filter (skip for Overseas/FX where taxes are 0) ---
        boolean isOverseas = "Overseas".equalsIgnoreCase(vendorSegment);
        List<Product> filteredProducts = isOverseas ? new ArrayList<>(byType)
                : filterByTaxSegment(byType, validTaxPrefixes, validTaxGids);

        if (filteredProducts.isEmpty()) {
            MasterLogger.warn("⚠️ No products matched tax criteria → falling back to same-type bucket.");
            filteredProducts = new ArrayList<>(byType);
        }
        if (filteredProducts.isEmpty()) {
            String msg = "❌ No valid " + desiredType + " products available after filtering.";
            if (desiredType != com.Vcidex.StoryboardSystems.Purchase.Model.ProductType.SERVICE)
                msg += " SERVICE products cannot be used for physical flows.";
            throw new RuntimeException(msg);
        }

        Collections.shuffle(filteredProducts);
        int pickCount = Math.min(faker.number().numberBetween(1, 6), filteredProducts.size());
        List<Product> picks = filteredProducts.subList(0, pickCount);

        List<LineItem> lineItems = new ArrayList<>();
        for (Product product : picks) {
            if (isOverseas) {
                // FX/Overseas => treat taxes as 0
                int qty = faker.number().numberBetween(1, 25);
                BigDecimal masterPrice;
                try { masterPrice = new BigDecimal(product.getProductPrice()); } catch (Exception e) { masterPrice = BigDecimal.ZERO; }
                BigDecimal unitPrice = masterPrice.compareTo(BigDecimal.ZERO) > 0
                        ? masterPrice
                        : BigDecimal.valueOf(faker.number().randomDouble(2, 100, 1000)).setScale(2, RoundingMode.HALF_UP);

                LineItem item = LineItem.builder()
                        .productGroup(product.getProductGroupName())
                        .productCode(product.getProductCode())
                        .productName(product.getProductName())
                        .description(product.getProductDesc())
                        .quantity(qty)
                        .price(unitPrice)
                        .discountPct(BigDecimal.ZERO)
                        .discountAmt(BigDecimal.ZERO)
                        .taxPrefix("0%")
                        .taxRate(BigDecimal.ZERO)
                        .product(product)
                        .build();
                item.computeTotal(); // Cannot resolve method 'computeTotal' in 'LineItem'
                lineItems.add(item);
            } else {
                // Domestic: enforce product tax linkage against vendor segment
                String chosenPrefix = null;
                String taxCode = product.getTax();
                String taxCode1 = product.getTax1();
                if (taxCode != null && (validTaxPrefixes.contains(taxCode.trim()) || validTaxGids.contains(taxCode.trim()))) {
                    chosenPrefix = taxCode.trim();
                } else if (taxCode1 != null && (validTaxPrefixes.contains(taxCode1.trim()) || validTaxGids.contains(taxCode1.trim()))) {
                    chosenPrefix = taxCode1.trim();
                } else {
                    MasterLogger.warn("⚠️ Skipping product: " + product.getProductName());
                    continue;
                }

                final String prefix = chosenPrefix;
                Tax tax = matchingTaxes.stream()
                        .filter(t -> prefix.equalsIgnoreCase(t.getTaxPrefix()) || prefix.equalsIgnoreCase(t.getTaxGid()))
                        .findFirst().orElse(null);

                BigDecimal taxRate = BigDecimal.ZERO;
                if (tax != null && tax.getPercentage() != null) {
                    try {
                        taxRate = new BigDecimal(tax.getPercentage())
                                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
                    } catch (NumberFormatException ex) {
                        MasterLogger.warn("⚠️ Invalid tax % for product: " + product.getProductName());
                    }
                }
                if (taxRate.compareTo(BigDecimal.ZERO) <= 0) {
                    MasterLogger.warn("⚠️ Skipping due to tax linkage issue: " + product.getProductName());
                    continue;
                }

                int quantity = faker.number().numberBetween(1, 25);
                BigDecimal masterPrice;
                try { masterPrice = new BigDecimal(product.getProductPrice()); } catch (Exception ex) { masterPrice = BigDecimal.ZERO; }

                BigDecimal price = masterPrice.compareTo(BigDecimal.ZERO) > 0
                        ? masterPrice
                        : BigDecimal.valueOf(faker.number().randomDouble(2, 500, 5000)).setScale(2, RoundingMode.HALF_UP);
                BigDecimal discountPct = BigDecimal.valueOf(random.nextDouble() * 15).setScale(2, RoundingMode.HALF_UP);
                BigDecimal discountAmt = price.multiply(BigDecimal.valueOf(quantity)).multiply(discountPct)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                LineItem item = LineItem.builder()
                        .productGroup(product.getProductGroupName())
                        .productCode(product.getProductCode())
                        .productName(product.getProductName())
                        .description(product.getProductDesc())
                        .quantity(quantity)
                        .price(price)
                        .discountPct(discountPct)
                        .discountAmt(discountAmt)
                        .taxPrefix(chosenPrefix)
                        .taxRate(taxRate)
                        .product(product)
                        .build();
                item.computeTotal();
                lineItems.add(item);
            }
        }

        if (lineItems.isEmpty()) {
            throw new RuntimeException("❌ All products skipped due to missing/invalid tax linkage for vendor: " + vendorName);
        }

        String mobile = faker.regexify("[6-9]\\d{9}");
        String nowTs = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String coverNote = "PO generated on " + nowTs + " — please inspect goods upon arrival.";

        // currency+rate driven by scenario
        String cur = input.currency();
        BigDecimal exRate = "INR".equalsIgnoreCase(cur) ? BigDecimal.ONE
                : BigDecimal.valueOf(faker.number().randomDouble(2, 1, 100)).setScale(2, RoundingMode.HALF_UP);

        PurchaseOrderData data = PurchaseOrderData.builder()
                .branchName(randomFrom(branchNames))
                .poRefNo("PO-" + faker.number().digits(6))   // placeholder; will be replaced by actual after submit
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
                .currency(cur)
                .exchangeRate(exRate)
                .coverNote(coverNote)
                .renewal(false)
                .renewalDate(null)
                .frequency(null)
                .lineItems(lineItems)
                .addOnCharges(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 500)))
                .additionalDiscount(BigDecimal.valueOf(random.nextDouble() * 15).setScale(2, RoundingMode.HALF_UP))
                .freightCharges(BigDecimal.valueOf(faker.number().randomDouble(2, 100, 300)))
                .additionalTax(matchingTaxes.isEmpty() ? "0%" : matchingTaxes.get(random.nextInt(matchingTaxes.size()))
                        .getPercentage().replaceAll("[^\\d.-]", "") + "%")
                .roundOff(BigDecimal.ZERO)
                .termsAndConditions(randomFrom(termTemplates))
                .termsEditorText("Ensure packaging standards are met.")
                .build();

        data.computeNetAmount();
        data.computeGrandTotal();
        data.setProductType(desiredType); // MODEL enum (what your POJO expects)
        return data;
    }
}