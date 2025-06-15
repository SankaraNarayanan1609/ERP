// File: src/main/java/com/Vcidex/StoryboardSystems/Inventory/MaterialInwardDataFactory.java
package com.Vcidex.StoryboardSystems.Inventory;

import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.LineItem;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MaterialInwardDataFactory {

    private static final Faker faker = new Faker(new Random(System.currentTimeMillis()));
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Create MaterialInwardData matching a specific number of rows.
     */
    public static MaterialInwardData create(int expectedRows) {
        MaterialInwardData d = new MaterialInwardData();

        String ts = LocalDate.now().format(TS_FMT)
                + faker.number().digits(3);

        d.setDcNo("DC" + ts);
        d.setTrackingNo("TRK" + ts);

        LocalDate today = LocalDate.now();
        LocalDate exp = today.plusDays(faker.number().numberBetween(1, 5));

        d.setGrnDate(today);
        d.setExpectedDate(exp);

        d.setDispatchMode(
                faker.options().option(
                        "AMAZON PICK UP", "APC - Courier Pack Next Day - CP16",
                        "APC - Liquids (Fragile)", "APC - Mail Pack Next Day - MP16",
                        "APC - NC Next Day - NC16", "Customer Collection",
                        "Driver Delivery", "Fedex Next Day Domestic",
                        "Translink Courier", "Royalmail 48 Tracking"
                )
        );

        d.setNoOfBoxes(String.valueOf(faker.number().numberBetween(1, 5)));

        d.setFilePaths(List.of(
                "/data/testdocs/" + faker.file().fileName(),
                "/data/testdocs/" + faker.file().fileName()
        ));

        Map<Integer, String> qtyMap = new LinkedHashMap<>();
        for (int i = 1; i <= expectedRows; i++) {
            qtyMap.put(i, String.valueOf(faker.number().numberBetween(0, 20)));
        }
        d.setReceivedQtyByRow(qtyMap);

        return d;
    }

    /**
     * Legacy: random row count, delegates to create(int).
     */
    public static MaterialInwardData createFromPO(PurchaseOrderData po) {
        List<LineItem> poLines = po.getLineItems();
        int rowCount = (poLines == null || poLines.isEmpty()) ? 1 : poLines.size();

        MaterialInwardData data = create(rowCount); // reuse existing generator

        Map<Integer, String> qtyMap = new LinkedHashMap<>();
        for (int i = 0; i < rowCount; i++) {
            LineItem line = poLines.get(i);
            int orderedQty = line.getQuantity(); // assuming quantity in LineItem
            qtyMap.put(i + 1, String.valueOf(orderedQty));
        }
        data.setReceivedQtyByRow(qtyMap);

        // Optional: set for visual assertion or reuse
//        data.setPoRefNo(po.getPoRefNo());
//        data.setVendorName(po.getVendorName());

        return data;
    }


}
