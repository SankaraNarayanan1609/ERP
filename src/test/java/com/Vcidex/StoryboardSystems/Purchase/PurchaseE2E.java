package com.Vcidex.StoryboardSystems.Purchase;

import com.Vcidex.StoryboardSystems.Common.NavigationManager;
import com.Vcidex.StoryboardSystems.Inventory.Navigation.MaterialInwardNavigator;
import com.Vcidex.StoryboardSystems.Inventory.Pages.Inward.MaterialInwardPage;
import com.Vcidex.StoryboardSystems.Purchase.Model.CurrencyKind;
import com.Vcidex.StoryboardSystems.Purchase.Model.FlowKind;
import com.Vcidex.StoryboardSystems.Purchase.Model.FlowSpec;
import com.Vcidex.StoryboardSystems.Purchase.Model.ProductType;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.PurchaseOrderNavigator;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.PaymentNavigator;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.PurchaseIndentNavigator;
import com.Vcidex.StoryboardSystems.Purchase.Navigation.ReceiveInvoiceNavigator;
import com.Vcidex.StoryboardSystems.Purchase.POJO.IndentData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PaymentData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseInvoiceData;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice.ReceiveInvoicePage;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.DirectPO;
import com.Vcidex.StoryboardSystems.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Support.PurchaseOrderMemory;
import com.Vcidex.StoryboardSystems.Utils.Config.ConfigManager;
import com.Vcidex.StoryboardSystems.Utils.DataFactory.PurchaseInvoiceDataFactory;
import com.Vcidex.StoryboardSystems.Utils.Logger.ReportManager;
import com.Vcidex.StoryboardSystems.Utils.Logger.ValidationLogger;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import com.aventstack.extentreports.ExtentTest;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

public class PurchaseE2E extends TestBase {

    @BeforeSuite(alwaysRun = true)
    public void beforeSuiteExtent() {
        ReportManager.init(ConfigManager.getEnv(), System.getProperty("build", "local"));
    }
    @AfterSuite(alwaysRun = true)
    public void afterSuiteExtent() { ReportManager.flush(); }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        ValidationLogger.reset();
    }

    @DataProvider(name = "flows")
    public static Object[][] flows() {
        return new Object[][]{
                { new FlowSpec(FlowKind.PI_BASED_PO,    ProductType.PHYSICAL, CurrencyKind.INR) },
                { new FlowSpec(FlowKind.PI_BASED_PO,    ProductType.PHYSICAL, CurrencyKind.FX)  },
                { new FlowSpec(FlowKind.PI_BASED_PO,    ProductType.SERVICE,  CurrencyKind.INR) },
                { new FlowSpec(FlowKind.PI_BASED_PO,    ProductType.SERVICE,  CurrencyKind.FX)  },
                { new FlowSpec(FlowKind.DIRECT_PO,      ProductType.PHYSICAL, CurrencyKind.INR) },
                { new FlowSpec(FlowKind.DIRECT_PO,      ProductType.PHYSICAL, CurrencyKind.FX)  },
                { new FlowSpec(FlowKind.DIRECT_PO,      ProductType.SERVICE,  CurrencyKind.INR) },
                { new FlowSpec(FlowKind.DIRECT_PO,      ProductType.SERVICE,  CurrencyKind.FX)  },
                { new FlowSpec(FlowKind.DIRECT_INVOICE, ProductType.SERVICE,  CurrencyKind.INR) },
                { new FlowSpec(FlowKind.DIRECT_INVOICE, ProductType.SERVICE,  CurrencyKind.FX)  }
        };
    }

    @Test(dataProvider = "flows")
    public void purchase_e2e(FlowSpec spec) {
        WebDriver driver = ThreadSafeDriverManager.getDriver();
        NavigationManager nav = new NavigationManager(driver);

        ExtentTest root = ReportManager.createTest("Purchase – " + spec, "Purchase", "E2E");
        ReportManager.setTest(root);

        var state = new PurchaseFlowState();

        // 🔹 Use factories from TestBase
        PurchaseOrderData poData = null;
        if (spec.needsPO()) {
            var forPO = toLegacyInput(spec);
            poData = poFactory.generateDataFor(forPO);
        }

        if (spec.needsPO()) {
            switch (spec.flow()) {
                case PI_BASED_PO -> {
                    var piNav = new PurchaseIndentNavigator(driver, nav, root);

                    var firstItem = poData.getLineItems() != null && !poData.getLineItems().isEmpty()
                            ? poData.getLineItems().get(0) : null;

                    // ✅ pre-wired indentFactory
                    var base = indentFactory.basicWithCostCenter(
                            poData.getBranchName(),
                            poData.getRequestedBy()
                    );

                    var indent = base.toBuilder()
                            .items(firstItem == null ? List.of()
                                    : List.of(IndentData.IndentItem.builder()
                                    .productName(firstItem.getProductName())
                                    .quantity(firstItem.getQuantity() <= 0 ? 1 : firstItem.getQuantity())
                                    .description("From automation")
                                    .neededBy(poData.getExpectedDate())
                                    .build()))
                            .remarks("Auto-PI for E2E")
                            .build();

                    String indentNo = piNav.createIndent(indent);

                    if (indentNo == null || indentNo.isBlank()) {
                        throw new IllegalStateException("Indent number not returned from PI submit. Check cost center/validations.");
                    }

                    var raisePage = piNav.raisePOFromIndent(poData.getBranchName(), indentNo);

                    String unitPrice = deriveUnitPrice(poData);
                    var res = raisePage.fillAndSubmit(
                            indentNo,                      // <-- NEW: so we can derive PO Ref
                            poData.getVendorName(),
                            unitPrice,
                            poData.getExpectedDate()
                    );

                    if (res.poNumber() == null || res.poNumber().isBlank()) {
                        throw new IllegalStateException("PO number not returned after submitting PO from Indent");
                    }
                    state.setPoRef(res.poNumber());
                    poData.setPoRefNo(res.poNumber());
                    PurchaseOrderMemory.set(res.poNumber());
                }
                case DIRECT_PO -> {
                    var poNav = new PurchaseOrderNavigator(driver, nav, root);
                    DirectPO poPage = poNav.openDirectPO();
                    poPage.fillForm(poData);
                    DirectPO.SubmitResult res = poPage.submitAndCapture();
                    ReportManager.info("🧾 Submit result → PO: " + String.valueOf(res.poNumber()));
                    if (res.poNumber() == null || res.poNumber().isBlank()) {
                        throw new IllegalStateException("PO number not returned after Direct PO submit");
                    }
                    state.setPoRef(res.poNumber());
                    poData.setPoRefNo(res.poNumber());
                    PurchaseOrderMemory.set(res.poNumber());
                }
                case DIRECT_INVOICE -> { /* no PO */ }
            }
        }

        // Decide flow based on spec: Service → Invoice; Physical → GRN then Invoice
        final boolean isServiceFlow = (spec.productType() == ProductType.SERVICE);

        // ===== PHYSICAL WITH PO → GRN FIRST =====
        if (spec.needsPO() && !isServiceFlow) {
            var inwardNav   = new MaterialInwardNavigator(driver, nav, root);
            MaterialInwardPage inwardPage = inwardNav.openMaterialInwardScreen();

            inwardPage.assertOnSelectPurchaseOrder(root);
            inwardPage.selectPurchaseOrder(state.getPoRef(), root);

            var inwardData = com.Vcidex.StoryboardSystems.Inventory.MaterialInwardDataFactory
                    .createFromPO(poData);

            inwardPage.clickAddInward(root);
            inwardPage.fillHeader(inwardData, root);
            inwardPage.selectDispatchMode(inwardData.getDispatchMode(), root);
            inwardPage.fillDispatchDetails(inwardData, root);
            inwardPage.fillInwardDetails(inwardData, root);

            String inwardNo = inwardPage.submitAndCapture(root);

            ReportManager.info("📦 Inward posted (GRN): " + inwardNo);
            if (inwardNo == null || inwardNo.isBlank()) {
                throw new IllegalStateException("Inward number not returned");
            }
            state.setGrnRef(inwardNo);
        }

        // ===== INVOICE (Service directly; Physical after GRN) =====
        PurchaseInvoiceData invoiceData = spec.needsPO()
                ? PurchaseInvoiceDataFactory.createFromPO(poData)
                : buildDirectInvoiceSeedWithoutAPI();

        var riNav  = new ReceiveInvoiceNavigator(driver, nav, root);
        ReceiveInvoicePage riPage = riNav.openReceiveInvoicePage(
                spec.needsPO() ? state.getPoRef() : null,
                /* isServiceTab */ isServiceFlow,
                (poData != null ? poData.getBranchName() : null)
        );

        riPage.fillInvoiceForm(invoiceData, root);

        String invoiceNo = submitAndCaptureInvoice(riPage, root);

        ReportManager.info("🧾 Invoice created: " + String.valueOf(invoiceNo));
        if (invoiceNo == null || invoiceNo.isBlank()) {
            throw new IllegalStateException("Invoice number not returned");
        }
        state.setInvoiceRef(invoiceNo);

        // ===== PAYMENT =====
        var payFactory = new com.Vcidex.StoryboardSystems.Utils.DataFactory.PaymentDataFactory();
        PaymentData payment = payFactory.createFromInvoice(invoiceData);

        var payNav = new PaymentNavigator(driver, nav, root);
        var payRes = payNav.pay(payment);

        ReportManager.info("💳 Payment posted: " + String.valueOf(payRes.paymentNo()));
        if (payRes.paymentNo() == null || payRes.paymentNo().isBlank()) {
            throw new IllegalStateException("Payment number not returned");
        }
        state.setPaymentRef(payRes.paymentNo());
    }

    private static PurchaseInvoiceData buildDirectInvoiceSeedWithoutAPI() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate due   = today.plusDays(10);
        String rand = String.valueOf(System.currentTimeMillis()).substring(8);
        return PurchaseInvoiceData.builder()
                .invoiceRefNo("PI-" + rand)
                .invoiceDate(today)
                .dueDate(due)
                .remarks("Auto direct-invoice")
                .purchaseType("Service")
                .billingEmail("billing@storyboarderp.com")
                .build();
    }

    private static String deriveUnitPrice(PurchaseOrderData po) {
        try {
            var items = po.getLineItems();
            if (items != null && !items.isEmpty() && items.get(0) != null) {
                Object first = items.get(0);
                for (String m : new String[]{"getUnitPrice","getPrice","getRate","getUnitCost","getCost"}) {
                    try {
                        Method mm = first.getClass().getMethod(m);
                        Object val = mm.invoke(first);
                        if (val instanceof BigDecimal bd) return bd.stripTrailingZeros().toPlainString();
                        if (val != null) return val.toString();
                    } catch (NoSuchMethodException ignore) {}
                }
            }
        } catch (Exception ignore) {}
        return "1";
    }

    private static String submitAndCaptureInvoice(ReceiveInvoicePage page, ExtentTest node) {
        page.submitInvoice(node);
        try {
            WebDriver d = com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager.getDriver();
            String body = d.findElement(org.openqa.selenium.By.tagName("body")).getText();
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("(INV[-/ ]?\\d{4,}|PI[-/ ]?\\d{4,}|Invoice\\s*No\\s*[:#]\\s*\\S+)", java.util.regex.Pattern.CASE_INSENSITIVE)
                    .matcher(body);
            if (m.find()) return m.group(0).replaceAll(".*[:#]\\s*", "").trim();
        } catch (Exception ignored) {}
        return null;
    }

    private static com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput toLegacyInput(FlowSpec s) {
        com.Vcidex.StoryboardSystems.Purchase.Model.EntryType entry =
                switch (s.flow()) {
                    case DIRECT_PO      -> com.Vcidex.StoryboardSystems.Purchase.Model.EntryType.DIRECT_PO;
                    case DIRECT_INVOICE -> com.Vcidex.StoryboardSystems.Purchase.Model.EntryType.DIRECT_INVOICE;
                    case PI_BASED_PO    -> com.Vcidex.StoryboardSystems.Purchase.Model.EntryType.PO_FROM_INDENT;
                };
        String currency = (s.currency() == CurrencyKind.INR) ? "INR" : "GBP";
        return new com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput(s.productType(), entry, currency);
    }

    private static com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput toLegacyDirectInvoiceInput(FlowSpec s) {
        String currency = (s.currency() == CurrencyKind.INR) ? "INR" : "GBP";
        return new com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseTestInput(
                s.productType(),
                com.Vcidex.StoryboardSystems.Purchase.Model.EntryType.DIRECT_INVOICE,
                currency
        );
    }
}