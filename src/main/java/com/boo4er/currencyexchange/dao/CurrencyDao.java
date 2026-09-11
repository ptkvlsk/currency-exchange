package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {
    public List<Currency> findAllCurrencies() throws  SQLException {
        List<Currency> currencies = new ArrayList<>();
        String sql = "SELECT id, code, full_name, sign FROM currencies";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Currency currency = new Currency();
                currency.setId(resultSet.getInt("id"));
                currency.setCode(resultSet.getString("code"));
                currency.setFullName(resultSet.getString("full_name"));
                currency.setSign(resultSet.getString("sign"));
                currencies.add(currency);
            }
        }
        return currencies;
    }

    public Currency findByCode(String code) throws   SQLException {
        String sql = "SELECT id, code, full_name, sign FROM currencies WHERE code = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Currency currency = new Currency();
                    currency.setId(resultSet.getInt("id"));
                    currency.setCode(resultSet.getString("code"));
                    currency.setFullName(resultSet.getString("full_name"));
                    currency.setSign(resultSet.getString("sign"));
                    return currency;
                }
            }
        }
        return null;
    }
}
