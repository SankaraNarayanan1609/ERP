package com.Vcidex.StoryboardSystems.Utils;

import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Tax;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaxService {
    private final Map<String, List<Tax>> taxesBySegment;

    public TaxService(List<Tax> allTaxes) {
        this.taxesBySegment = allTaxes.stream()
                .filter(t -> t.getTaxsegment_name()!=null)
                .collect(Collectors.groupingBy(t->t.getTaxsegment_name().trim().toLowerCase()));
    }

    /**
     * Find the one Tax entry for this product + segment,
     * matching either tax_prefix or tax_gid to product.getTax()/getTax1().
     */
    public Tax lookup(Product p, String vendorSegment) {
        List<Tax> segTaxes = taxesBySegment
                .getOrDefault(vendorSegment.trim().toLowerCase(), Collections.emptyList());
        String t0 = p.getTax(), t1 = p.getTax1();
        return Stream.of(t0,t1)
                .filter(Objects::nonNull)
                .map(String::trim)
                .flatMap(code -> segTaxes.stream()
                        .filter(tx -> code.equalsIgnoreCase(tx.getTaxPrefix())
                                || code.equalsIgnoreCase(tx.getTaxGid())))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No Tax found for product " + p.getProductName() + " in segment " + vendorSegment));
    }
}

