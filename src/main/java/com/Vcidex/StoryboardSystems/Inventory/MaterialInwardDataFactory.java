// File: src/main/java/com/Vcidex/StoryboardSystems/Inventory/MaterialInwardDataFactory.java
/**
 * Factory class to create realistic `MaterialInwardData` objects.
 * These objects represent the data used for performing GRN (Goods Receipt Note)
 * actions based on a Purchase Order or randomly.
 */

package com.Vcidex.StoryboardSystems.Inventory;

import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MaterialInwardDataFactory {

    // Faker helps generate fake but realistic data like numbers, strings, files, etc.
    private static final Faker faker = new Faker(new Random(System.currentTimeMillis()));

    // Used to create timestamp strings like 20240618 (format: yyyyMMdd)
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Creates a `MaterialInwardData` object with a fixed number of line items.
     * Each line will have a randomly generated received quantity.
     *
     * @param expectedRows number of rows/line-items in the GRN
     * @return MaterialInwardData with generated values
     */
    public static MaterialInwardData create(int expectedRows) {
        MaterialInwardData d = new MaterialInwardData();

        // Generate timestamp-like suffix for DC and tracking numbers
        String ts = LocalDate.now().format(TS_FMT) + faker.number().digits(3);
        d.setDcNo("DC" + ts);          // e.g., DC20240618123
        d.setTrackingNo("TRK" + ts);   // e.g., TRK20240618123

        // Set today's date and expected delivery date (1–5 days later)
        LocalDate today = LocalDate.now();
        LocalDate exp = today.plusDays(faker.number().numberBetween(1, 5));
        d.setGrnDate(today);
        d.setExpectedDate(exp);

        // Randomly select one from realistic dispatch modes
        d.setDispatchMode(
                faker.options().option(
                        "AMAZON PICK UP", "APC - Courier Pack Next Day - CP16",
                        "APC - Liquids (Fragile)", "APC - Mail Pack Next Day - MP16",
                        "APC - NC Next Day - NC16", "Customer Collection",
                        "Driver Delivery", "Fedex Next Day Domestic",
                        "Translink Courier", "Royalmail 48 Tracking"
                )
        );

        // Set a random number of boxes (as string)
        d.setNoOfBoxes(String.valueOf(faker.number().numberBetween(1, 5)));

        // Simulate document upload by generating fake file paths
        d.setFilePaths(List.of(
                "/data/testdocs/" + faker.file().fileName(),
                "/data/testdocs/" + faker.file().fileName()
        ));

        // Set random received quantity for each expected line
        Map<Integer, String> qtyMap = new LinkedHashMap<>();
        for (int i = 1; i <= expectedRows; i++) {
            qtyMap.put(i, String.valueOf(faker.number().numberBetween(0, 20)));
        }
        d.setReceivedQtyByRow(qtyMap);

        return d;
    }

    /**
     * Generates a MaterialInwardData based on a given PurchaseOrderData object.
     * Ensures that received quantities match ordered quantities.
     *
     * @param po PurchaseOrderData object used as reference
     * @return MaterialInwardData matching PO line items
     */
    public static MaterialInwardData createFromPO(PurchaseOrderData po) {
        List<LineItem> poLines = po.getLineItems();

        // Determine how many rows/line-items exist in the PO
        int rowCount = (poLines == null || poLines.isEmpty()) ? 1 : poLines.size();

        // Call main create() logic
        MaterialInwardData data = create(rowCount);

        // Now override with exact quantity values from the PO
        Map<Integer, String> qtyMap = new LinkedHashMap<>();
        for (int i = 0; i < rowCount; i++) {
            LineItem line = poLines.get(i);
            int orderedQty = line.getQuantity();  // Fetch ordered quantity
            qtyMap.put(i + 1, String.valueOf(orderedQty)); // Set it as received quantity
        }
        data.setReceivedQtyByRow(qtyMap);

        // Optional fields can be set for reference or verification
        // data.setPoRefNo(po.getPoRefNo());
        // data.setVendorName(po.getVendorName());

        return data;
    }
}