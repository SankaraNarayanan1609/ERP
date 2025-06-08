package com.Vcidex.StoryboardSystems.Inventory;

import com.Vcidex.StoryboardSystems.Inventory.POJO.MaterialInwardData;
import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MaterialInwardDataFactory {
    // seed Faker with current time so each run is unique but still “fake”
    private static final Faker faker = new Faker(new Random(System.currentTimeMillis()));
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static MaterialInwardData create() {
        MaterialInwardData d = new MaterialInwardData();

        // use timestamp for DC & Tracking to guarantee real-time uniqueness
        String ts = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + faker.number().digits(3);

        d.setDcNo("DC" + ts);
        d.setTrackingNo("TRK" + ts);

        // real current date & a near-future expected date
        String today = LocalDate.now().format(DATE_FMT);
        String exp   = LocalDate.now()
                .plusDays(faker.number().numberBetween(1,5))
                .format(DATE_FMT);

        d.setGrnDate(today);
        d.setExpectedDate(exp);

        // realistic dispatch modes
        d.setDispatchMode(
                faker.options().option("Courier", "Road", "Air", "Sea")
        );

        d.setNoOfBoxes(
                String.valueOf(faker.number().numberBetween(1,5))
        );

        // your real test-file paths (adjust as needed)
        d.setFilePaths(List.of(
                "/data/testdocs/" + faker.file().fileName(),
                "/data/testdocs/" + faker.file().fileName()
        ));

        // simulate 1–3 table rows
        int rows = faker.number().numberBetween(1,4);
        Map<Integer,String> qtyMap = new LinkedHashMap<>();
        for (int i = 1; i <= rows; i++) {
            // ensure received ≤ some realistic max
            qtyMap.put(i, String.valueOf(faker.number().numberBetween(0, 20)));
        }
        d.setReceivedQtyByRow(qtyMap);

        return d;
    }
}

