package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.exception.ConflictException;
import com.boo4er.currencyexchange.exception.DatabaseException;
import com.boo4er.currencyexchange.exception.NotFoundException;
import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CurrencyDao {

    public List<Currency> findAll() throws AppException {
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
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при получении списка валют:", e);
        }
        return currencies;
    }

    public Currency findByCode(String code) throws AppException {
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
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске валюты: ", e);
        }
        throw new NotFoundException("Валюта с кодом " + code + " не найдена");
    }

    public Currency save(Currency currency) throws AppException {
        String sql = "INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    currency.setId(keys.getInt(1));
                }
            }
            return currency;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint")) {
                throw new ConflictException("Валюта с кодом " + currency.getCode() + " уже существует");
            }
            throw new DatabaseException("Ошибка при сохранении валюты", e);
        }
    }
}
