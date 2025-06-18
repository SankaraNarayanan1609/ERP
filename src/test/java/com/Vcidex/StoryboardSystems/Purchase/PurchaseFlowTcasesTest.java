//// PurchaseFlowTcasesTest.java
//package com.Vcidex.StoryboardSystems.Purchase;
//
//
//import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
//import com.Vcidex.StoryboardSystems.TestBase;
//
//import org.testng.ITestContext;
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//import org.openqa.selenium.WebDriver;
//
//import java.util.List;
//
//public class PurchaseFlowTcasesTest extends TestBase {
////    private static final Model model = new ModelBuilder() // Cannot resolve symbol 'Model' 'ModelBuilder'
//            .stringParam("entryType",      List.of("PI_PO","DIRECT_PO","DIRECT_INVOICE","PURCHASE_AGREEMENT"))
//            .stringParam("productType",    List.of("PHYSICAL","SERVICE"))
//            .stringParam("paymentStyle",   List.of("FULL","PARTIAL","MULTI"))
//            .intParam(   "rejectCount",    List.of(0,3))
//            .stringParam("vendorCurrency", List.of("INR","USD"))
//            .build();
//
//    // in PurchaseFlowTcasesTest
//    private final TcasesPurchaseDataFactory dataFactory =
//            new TcasesPurchaseDataFactory(TestBase.factory);
//
//    @DataProvider(name="twiseScenarios", parallel=true)
//    public Object[][] twiseScenarios(ITestContext ctx) {
//        // read tWay from XML, default=2
//        String tWay = ctx.getCurrentXmlTest().getParameter("tWay");
//        int ways = tWay!=null ? Integer.parseInt(tWay) : 2;
//        CoverageStrategy strat = switch (ways) { // Cannot resolve symbol 'CoverageStrategy'
//            case 3 -> CoverageStrategy.tripleWise();
//            case 4 -> CoverageStrategy.fourWise();
//            default -> CoverageStrategy.pairwise();
//        };
//
//        TestSuite suite = new TestSuiteGenerator(model, strategy).generate(); // Cannot resolve symbol 'TestSuite''TestSuiteGenerator''strategy'
//        return suite.getTestCases().stream() // Cannot resolve method 'getTestCases()'
//                .map(tc -> new Object[]{
//                        // 2nd arg is your scen if you need it:
//                        dataFactory.createWithOverrides(tc, /* isRenewal? */ false)
//                })
//                .toArray(Object[][]::new);
//    }
//
//    @Test(dataProvider="twiseScenarios")
//    public void runDynamicFlow(PurchaseOrderData data) {
//        WebDriver driver = this.driver;        // from TestBase
//        nav.goToModule("Purchase");
//        new DynamicPurchaseExecutor(driver, data, ScenarioDecider.fromData(data))
//                .execute();
//        // …your post-flow assertions…
//    }
//}