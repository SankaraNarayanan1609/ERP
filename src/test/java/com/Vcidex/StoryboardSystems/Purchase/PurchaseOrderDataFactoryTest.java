package com.Vcidex.StoryboardSystems.Purchase;

// TestNG annotations & assertions
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

// Java collections
import java.util.List;
import java.util.Set;

// Classes under test
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;
import java.util.Collections;
import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Tax;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;

public class PurchaseOrderDataFactoryTest {

    private PurchaseOrderDataFactory factory;

    @BeforeClass
    public void setup() {
        // Pass any non-empty strings so constructor doesn’t throw
        ApiMasterDataProvider stubProvider = new ApiMasterDataProvider(
                "http://dummy-base-url",   // baseUrl
                "Bearer dummy-token"       // authToken (the “Bearer ” prefix is stripped internally)
        ) {
            @Override public List<Vendor> getVendors()            { return Collections.emptyList(); }
            @Override public List<String> getBranches()           { return Collections.emptyList(); }
            @Override public List<String> getCurrencies()         { return Collections.emptyList(); }
            @Override public List<String> getTermsAndConditions() { return Collections.emptyList(); }
            @Override public List<Product> getProducts()          { return Collections.emptyList(); }
            @Override public List<Tax> getAllTaxObjects()         { return Collections.emptyList(); }
            @Override public List<Employee> getEmployees()        { return Collections.emptyList(); }
        };
        factory = new PurchaseOrderDataFactory(stubProvider);
    }

    @Test
    public void testFilterByTaxSegment() {
        // 1) Prepare fake prefixes/GIDs
        Set<String> prefixes = Set.of("VAT 20%", "VAT 0%");
        Set<String> gids     = Set.of("GID123");

        // 2) Create products
        Product matchPrefix = new Product();
        matchPrefix.setProductName("A"); // Cannot resolve method 'setProductName' in 'Product'
        matchPrefix.setTax("VAT 20%");        // should pass //Cannot resolve method 'setTax' in 'Product'
        matchPrefix.setTax1(null); // Cannot resolve method 'setTax1' in 'Product'

        Product matchGid = new Product();
        matchGid.setProductName("B");
        matchGid.setTax(null);
        matchGid.setTax1("GID123");           // should pass

        Product noMatch = new Product();
        noMatch.setProductName("C");
        noMatch.setTax("GST 5%");             // should be dropped
        noMatch.setTax1("OTHER");

        List<Product> input = List.of(matchPrefix, matchGid, noMatch);

        // 3) Call filter
        List<Product> result = factory.filterByTaxSegment(input, prefixes, gids);

        // 4) Verify
        Assert.assertEquals(result.size(), 2, "Expected two matching products");
        List<String> names = result.stream().map(Product::getProductName).toList();
        Assert.assertTrue(names.containsAll(List.of("A", "B")));
    }
}
