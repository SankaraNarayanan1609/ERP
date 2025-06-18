//// TcasesPurchaseDataFactory.java
//package com.Vcidex.StoryboardSystems.Utils.DataFactory;
//
//import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
//import com.Vcidex.StoryboardSystems.Purchase.POJO.*;
//
//import org.cornutum.tcases.TestCase;
//
//import java.util.Map;
//
//public class TcasesPurchaseDataFactory {
//    private final PurchaseOrderDataFactory baseFactory;
//
//    public TcasesPurchaseDataFactory(ApiMasterDataProvider apiProvider) {
//        this.baseFactory = new PurchaseOrderDataFactory(apiProvider);
//    }
//
//    /**
//     * Build base data, then overlay the t-cases parameters
//     * from a single TestCase’s param map.
//     */
//    public PurchaseOrderData createWithOverrides(TestCase tc, boolean isRenewal) {
//        // 1) build all the faker data as before
//        PurchaseOrderData base = baseFactory.create(isRenewal);
//
//    }
//}
