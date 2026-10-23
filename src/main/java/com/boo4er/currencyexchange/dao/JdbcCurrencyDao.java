package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.exception.AlreadyExistsException;
import com.boo4er.currencyexchange.exception.DatabaseException;
import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyDao implements CurrencyDao {

    private static final String SELECT_ALL_SQL = "SELECT id, code, full_name, sign FROM currencies";
    private static final String SELECT_BY_CODE_SQL = SELECT_ALL_SQL + " WHERE code = ?";
    private static final String INSERT_SQL = """
            INSERT INTO currencies (code, full_name, sign) VALUES (?, ?, ?)
            RETURNING id, code, full_name, sign
            """;

    @Override
    public List<Currency> findAll() {
        List<Currency> currencies = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                currencies.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при получении списка валют", e);
        }
        return currencies;
    }

    @Override
    public Optional<Currency> findByCode(String code) {

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_BY_CODE_SQL)) {

            preparedStatement.setString(1, code);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске валюты", e);
        }
        return Optional.empty();
    }

    @Override
    public Currency save(Currency currency) {

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(INSERT_SQL)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
            throw new DatabaseException("База не вернула сохранённую валюту");

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint")) {
                throw new AlreadyExistsException("Валюта с кодом "
                        + currency.getCode() + " уже существует");
            }
            throw new DatabaseException("Ошибка при сохранении валюты", e);
        }
    }

    private Currency mapRow(ResultSet resultSet) throws SQLException {
        Currency currency = new Currency();
        currency.setId(resultSet.getInt("id"));
        currency.setCode(resultSet.getString("code"));
        currency.setFullName(resultSet.getString("full_name"));
        currency.setSign(resultSet.getString("sign"));
        return currency;
    }
}
