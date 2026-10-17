package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.exception.ConflictException;
import com.boo4er.currencyexchange.exception.DatabaseException;
import com.boo4er.currencyexchange.exception.NotFoundException;
import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.model.ExchangeRate;
import com.boo4er.currencyexchange.util.CurrencyCodeUtil;
import com.boo4er.currencyexchange.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcExchangeRateDao {

    private static final String SELECT_SQL = """
            SELECT
                er.id, er.rate,
                bc.id AS base_id, bc.code AS base_code, bc.full_name AS base_full_name, bc.sign AS base_sign,
                tc.id AS target_id, tc.code AS target_code, tc.full_name AS target_full_name, tc.sign AS target_sign
            FROM exchange_rates er
            JOIN currencies bc ON er.base_currency_id = bc.id
            JOIN currencies tc ON er.target_currency_id = tc.id
            """;

    private static final String SELECT_BY_PAIR_SQL =
            SELECT_SQL + "WHERE bc.code = ? AND tc.code = ?";

    private static final String INSERT_SQL = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            RETURNING id, rate
            """;

    private static final String UPDATE_SQL = """
            UPDATE exchange_rates SET rate = ?
            WHERE base_currency_id = (SELECT id FROM currencies WHERE code = ?)
              AND target_currency_id = (SELECT id FROM currencies WHERE code = ?)
            """;

    public List<ExchangeRate> findAll() {
        List<ExchangeRate> rates = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                rates.add(mapRow(resultSet));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при получении списка курсов", e);
        }
        return rates;
    }

    public ExchangeRate findByPair(String baseCode, String targetCode) {

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(SELECT_BY_PAIR_SQL)) {

            preparedStatement.setString(1, CurrencyCodeUtil.normalize(baseCode));
            preparedStatement.setString(2, CurrencyCodeUtil.normalize(targetCode));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске курса", e);
        }
        throw new NotFoundException("Курс для пары " + CurrencyCodeUtil.normalize(baseCode)
                + " " + CurrencyCodeUtil.normalize(targetCode) + " не найден");
    }

    public ExchangeRate save(ExchangeRate exchangeRate) {

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(INSERT_SQL)) {

            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ExchangeRate(resultSet.getInt("id"), exchangeRate.getBaseCurrency(),
                            exchangeRate.getTargetCurrency(), resultSet.getBigDecimal("rate"));
                }
            }
            throw new DatabaseException("База не вернула сохранённый курс");
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint")) {
                throw new ConflictException("Валютная пара " + exchangeRate.getBaseCurrency().getCode() +
                        " " + exchangeRate.getTargetCurrency().getCode() + " уже существует");
            }
            throw new DatabaseException("Ошибка при создании курса", e);
        }
    }

    public ExchangeRate update(String baseCode, String targetCode, BigDecimal newRate) {

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setBigDecimal(1, newRate);
            preparedStatement.setString(2, CurrencyCodeUtil.normalize(baseCode));
            preparedStatement.setString(3, CurrencyCodeUtil.normalize(targetCode));

            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                throw new NotFoundException("Курс для пары " + CurrencyCodeUtil.normalize(baseCode)
                        + " " + CurrencyCodeUtil.normalize(targetCode) + " не найден");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при обновлении курса", e);
        }
        return findByPair(baseCode, targetCode);
    }

    private Currency mapCurrency(ResultSet resultSet, String prefix) throws SQLException {
        Currency currency = new Currency();
        currency.setId(resultSet.getInt(prefix + "id"));
        currency.setCode(resultSet.getString(prefix + "code"));
        currency.setFullName(resultSet.getString(prefix + "full_name"));
        currency.setSign(resultSet.getString(prefix + "sign"));
        return currency;
    }

    private ExchangeRate mapRow(ResultSet resultSet) throws SQLException {
        ExchangeRate rate = new ExchangeRate();
        rate.setId(resultSet.getInt("id"));
        rate.setBaseCurrency(mapCurrency(resultSet, "base_"));
        rate.setTargetCurrency(mapCurrency(resultSet, "target_"));
        rate.setRate(resultSet.getBigDecimal("rate"));
        return rate;
    }
}

