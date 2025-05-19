package com.Vcidex.StoryboardSystems.Purchase.Factory;

import com.Vcidex.StoryboardSystems.Purchase.POJO.Vendor;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Employee;
import com.Vcidex.StoryboardSystems.Purchase.POJO.Product;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class DbMasterDataProvider implements MasterDataProvider {
    private final DataSource dataSource;

    public DbMasterDataProvider(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> getBranches() {
        return queryForList(
                "SELECT branch_name FROM branch_master",
                rs -> rs.getString(1)
        );
    }

    @Override
    public List<Vendor> getVendors() {
        return queryForList(
                "SELECT vendor_id, vendor_name, details, branch_address FROM vendor_master",
                rs -> {
                    Vendor v = new Vendor();
                    v.setId(rs.getLong("vendor_id"));
                    v.setName(rs.getString("vendor_name"));
                    v.setDetails(rs.getString("details"));
                    v.setBranchAddress(rs.getString("branch_address"));
                    return v;
                }
        );
    }

    @Override
    public List<Employee> getEmployees() {
        return queryForList(
                "SELECT emp_id, emp_name, contact FROM employee_master",
                rs -> {
                    Employee e = new Employee();
                    e.setId(rs.getLong("emp_id"));
                    e.setName(rs.getString("emp_name"));
                    e.setContact(rs.getString("contact"));
                    return e;
                }
        );
    }

    @Override
    public List<Product> getProducts() {
        return queryForList(
                "SELECT prod_group, prod_code, prod_name, description, price, tax_rate FROM product_master",
                rs -> {
                    Product p = new Product();
                    p.setGroup(rs.getString("prod_group"));
                    p.setCode(rs.getString("prod_code"));
                    p.setName(rs.getString("prod_name"));
                    p.setDescription(rs.getString("description"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setTaxRate(rs.getBigDecimal("tax_rate"));
                    return p;
                }
        );
    }

    @Override
    public List<String> getCurrencies() {
        return queryForList(
                "SELECT currency_code FROM currency_master",
                rs -> rs.getString(1)
        );
    }

    @Override
    public Map<String, BigDecimal> getCurrencyRates() {
        String sql = "SELECT currency_code, exchange_rate FROM currency_master";
        Map<String, BigDecimal> rates = new HashMap<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                rates.put(
                        rs.getString("currency_code"),
                        rs.getBigDecimal("exchange_rate")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load currency rates", e);
        }
        return rates;
    }

    @Override
    public List<String> getTermsAndConditions() {
        return queryForList(
                "SELECT code FROM terms_conditions_master",
                rs -> rs.getString("code")
        );
    }

    @Override
    public List<String> getAdditionalTaxCodes() {
        return queryForList(
                "SELECT tax_code FROM tax_codes_master",
                rs -> rs.getString("tax_code")
        );
    }

    // ----------------------------------------------------------------
    // Generic helper for mapping a single-column or row-to-object query
    private <T> List<T> queryForList(
            String sql,
            SqlMapper<T> mapper
    ) {
        List<T> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + sql, e);
        }
        return list;
    }

    @FunctionalInterface
    private interface SqlMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}