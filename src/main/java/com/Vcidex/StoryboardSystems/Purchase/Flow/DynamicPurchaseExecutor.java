package com.Vcidex.StoryboardSystems.Purchase.Flow;

import com.Vcidex.StoryboardSystems.Purchase.Flow.Commands.*;
import com.Vcidex.StoryboardSystems.Purchase.Model.EntryType;
import com.Vcidex.StoryboardSystems.Purchase.Model.PurchaseScenario;
import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;

import org.openqa.selenium.WebDriver;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class DynamicPurchaseExecutor {
    private final WebDriver driver;
    private final PurchaseOrderData data;
    private final PurchaseScenario scen;

    // ← fixed: explicit type‐witness so compiler treats all values as StepCommand
    private static final Map<Step, StepCommand> commands = Map.<Step, StepCommand>of(
            Step.INDENT,     new PurchaseIndentCommand(),
            Step.DirectPO,   new DirectPOCommand(),
            Step.GRN,        new GRNCommand(),
            Step.RETURN,     new PurchaseReturnCommand(),
            Step.DEBIT_NOTE, new DebitNoteCommand(),
            Step.INVOICE,    new ReceiveInvoiceCommand(),
            Step.PAYMENT,    new PaymentCommand()
    );

    private static final Map<Step, Function<PurchaseScenario, Optional<Step>>> nextStep = Map.of(
            Step.INDENT,     s -> Optional.of(Step.RAISE_PO),
            Step.RAISE_PO,   s -> s.hasGRN ? Optional.of(Step.GRN) : Optional.of(Step.INVOICE),
            Step.GRN,        s -> s.rejectCount > 0 ? Optional.of(Step.RETURN) : Optional.of(Step.INVOICE),
            Step.RETURN,     s -> Optional.of(Step.DEBIT_NOTE),
            Step.DEBIT_NOTE, s -> Optional.of(Step.INVOICE),
            Step.INVOICE,    s -> Optional.of(Step.PAYMENT),
            Step.PAYMENT,    s -> Optional.empty()
    );

    public DynamicPurchaseExecutor(WebDriver driver,
                                   PurchaseOrderData data,
                                   PurchaseScenario scen) {
        this.driver = driver;
        this.data   = data;
        this.scen   = scen;
    }

    private Step getStartStep(EntryType e) {
        return switch (e) {
            case PI_PO             -> Step.INDENT;
            case DIRECT_PO         -> Step.RAISE_PO;
            case DIRECT_INVOICE,
                    PURCHASE_AGREEMENT-> Step.INVOICE;
        };
    }

    public void execute() {
        Step current = getStartStep(scen.entryType);
        while (true) {
            commands.get(current).execute(driver, data, scen);
            Optional<Step> next = nextStep.get(current).apply(scen);
            if (next.isEmpty()) break;
            current = next.get();
        }
    }

}