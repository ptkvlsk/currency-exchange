package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.exception.ConflictException;
import com.boo4er.currencyexchange.exception.DatabaseException;
import com.boo4er.currencyexchange.exception.NotFoundException;
import com.boo4er.currencyexchange.model.ExchangeRate;
import com.boo4er.currencyexchange.util.DatabaseUtil;
import com.boo4er.currencyexchange.model.Currency;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao {
    public List<ExchangeRate> findAll() throws AppException {
        List<ExchangeRate> rates = new ArrayList<>();

        String sql = """
                SELECT
                    er.id, er.rate,
                    bc.id AS base_id, bc.code AS base_code, bc.full_name AS base_full_name, bc.sign AS base_sign,
                    tc.id AS target_id, tc.code AS target_code, tc.full_name AS target_full_name, tc.sign AS target_sign
                FROM exchange_rates er
                JOIN currencies bc ON er.base_currency_id = bc.id
                JOIN currencies tc ON er.target_currency_id = tc.id
                """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                Currency base = new Currency();
                base.setId(resultSet.getInt("base_id"));
                base.setCode(resultSet.getString("base_code"));
                base.setFullName(resultSet.getString("base_full_name"));
                base.setSign(resultSet.getString("base_sign"));

                Currency target = new Currency();
                target.setId(resultSet.getInt("target_id"));
                target.setCode(resultSet.getString("target_code"));
                target.setFullName(resultSet.getString("target_full_name"));
                target.setSign(resultSet.getString("target_sign"));

                ExchangeRate exchangeRate = new ExchangeRate();
                exchangeRate.setId(resultSet.getInt("id"));
                exchangeRate.setBaseCurrency(base);
                exchangeRate.setTargetCurrency(target);
                exchangeRate.setRate(resultSet.getBigDecimal("rate"));

                rates.add(exchangeRate);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при получении списка курсов:", e);
        }
        return rates;
    }

    public ExchangeRate findByPair(String baseCode, String targetCode) throws AppException {
        String sql = """
                SELECT
                er.id, er.rate,
                        bc.id AS base_id, bc.code AS base_code, bc.full_name AS base_full_name, bc.sign AS base_sign,
                        tc.id AS target_id, tc.code AS target_code, tc.full_name AS target_full_name, tc.sign AS target_sign
                FROM exchange_rates er
                JOIN currencies bc ON er.base_currency_id = bc.id
                JOIN currencies tc ON er.target_currency_id = tc.id
                WHERE bc.code = ? AND tc.code = ?
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Currency base = new Currency();
                    base.setId(resultSet.getInt("base_id"));
                    base.setCode(resultSet.getString("base_code"));
                    base.setFullName(resultSet.getString("base_full_name"));
                    base.setSign(resultSet.getString("base_sign"));

                    Currency target = new Currency();
                    target.setId(resultSet.getInt("target_id"));
                    target.setCode(resultSet.getString("target_code"));
                    target.setFullName(resultSet.getString("target_full_name"));
                    target.setSign(resultSet.getString("target_sign"));

                    ExchangeRate exchangeRate = new ExchangeRate();
                    exchangeRate.setId(resultSet.getInt("id"));
                    exchangeRate.setBaseCurrency(base);
                    exchangeRate.setTargetCurrency(target);
                    exchangeRate.setRate(resultSet.getBigDecimal("rate"));

                    return exchangeRate;
                }

            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при поиске курса", e);
        }
        throw new NotFoundException("Курс для пары " + baseCode + " " + targetCode + " не найден");
    }

    public ExchangeRate save(ExchangeRate exchangeRate) throws AppException {
        String sql = "INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, exchangeRate.getBaseCurrency().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());

            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    exchangeRate.setId(keys.getInt(1));
                }
            }
            return exchangeRate;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE constraint")) {
                throw new ConflictException("Валютная пара " + exchangeRate.getBaseCurrency().getCode() +
                        " " + exchangeRate.getTargetCurrency().getCode() + " уже существует");
            }
            throw new DatabaseException("Ошибка при создании курса", e);
        }
    }

    public ExchangeRate update(String baseCode, String targetCode, BigDecimal newRate) throws AppException {

        String sql = """
                UPDATE exchange_rates SET rate = ?
                WHERE base_currency_id = ( SELECT id FROM currencies WHERE code =?)
                AND target_currency_id = ( SELECT id FROM currencies WHERE code = ?)
                """;

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setBigDecimal(1, newRate);
            preparedStatement.setString(2, baseCode);
            preparedStatement.setString(3, targetCode);

            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                throw new NotFoundException("Курс для пары " + baseCode + " " + targetCode + " не найден");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка при обновлении курса", e);
        }
        return findByPair(baseCode, targetCode);
    }
}

