package com.Vcidex.StoryboardSystems.Utils.Data.DataFactory;

import com.github.javafaker.Faker;
import org.apache.commons.math3.stat.descriptive.summary.Product;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ProductDataFactory {

    private static final List<String> PRODUCT_NAMES = Arrays.asList(
            "Laptop", "Smartphone", "Keyboard", "Headphones", "Monitor",
            "Mouse", "Tablet", "Webcam", "Charger", "Power Bank"
    );

    private static final Faker faker = new Faker();
    private static final Random random = new Random();

    public static Product createRandomProduct() {
        String productName = PRODUCT_NAMES.get(random.nextInt(PRODUCT_NAMES.size()));
        int quantity = faker.number().numberBetween(1, 100);
        double price = faker.number().randomDouble(2, 1000, 50000);
        double discount = faker.number().randomDouble(1, 0, 50); // discount in %

        return new Product();
    }
}

