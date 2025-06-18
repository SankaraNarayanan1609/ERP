package com.Vcidex.StoryboardSystems.Purchase.Model;

public enum EntryType {
    PI_PO,          // PI → Raise PO
    DIRECT_PO,      // Direct PO
    DIRECT_INVOICE, // Invoice only
    PURCHASE_AGREEMENT // Agreement → Invoice
}
