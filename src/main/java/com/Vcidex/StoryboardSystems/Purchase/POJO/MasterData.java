/**
 * MasterData is a container class that holds all reference (static) data needed
 * for Purchase Order (PO), Invoice, GRN, and Payment flows.
 *
 * This data is usually fetched via a REST API at the beginning of the test session.
 * It ensures dropdowns, validations, and business logic align with actual system data.
 *
 * Each field maps to a different type of master entity:
 *  - Branch:     Company branches (locations)
 *  - Employee:   System users (creators, approvers)
 *  - Vendor:     Suppliers available for PO
 *  - Product:    Items that can be purchased
 *  - Tax:        Applicable tax configurations
 *  - Terms:      T&C templates for PO footer
 *  - Currency:   Currencies and exchange rates
 */

package com.Vcidex.StoryboardSystems.Purchase.POJO;

import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Branch;
import com.Vcidex.StoryboardSystems.CmnMasterPOJO.Employee;

import java.util.List;
import java.util.Map;

public class MasterData {

    /**
     * List of available branches in the system.
     * Used in PO header for 'Deliver To' or 'Created From'.
     */
    public List<Branch> branches;

    /**
     * List of employees/users who can be creators or approvers.
     */
    public List<Employee> employees;

    /**
     * List of active vendors/suppliers for PO selection.
     */
    public List<Vendor> vendors;

    /**
     * List of purchasable products.
     * Used in line items for PO/Invoice.
     */
    public List<Product> products;

    /**
     * Tax configurations available in the system.
     * Used for applying GST/VAT in line items or PO total.
     */
    public List<Tax> taxes;

    /**
     * Standard terms and conditions templates.
     * Used to populate PO footer automatically.
     */
    public List<TermsAndConditions> terms;

    /**
     * List of currency data with exchange rates.
     * Each map contains:
     *    - currency_code: INR, USD, etc.
     *    - exchange_rate: 83.0, 1.0 etc.
     *
     * The use of Map<String, String> is to match dynamic API format.
     */
    public List<Map<String, String>> currencies;

    public List<CostCenter> costCenters;
}