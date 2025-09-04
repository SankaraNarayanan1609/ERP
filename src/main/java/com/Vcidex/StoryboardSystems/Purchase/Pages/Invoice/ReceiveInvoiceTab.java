package com.Vcidex.StoryboardSystems.Purchase.Pages.Invoice;

/** Small domain enum → keeps tab choice out of the navigator. */
public enum ReceiveInvoiceTab {
    PRODUCTS, SERVICE;

    /** Maps to current DOM pane ids; closed for change, open for extension. */
    public String paneId() {
        return this == SERVICE ? "kt_tab_pane_3" : "kt_tab_pane_2";
    }
}