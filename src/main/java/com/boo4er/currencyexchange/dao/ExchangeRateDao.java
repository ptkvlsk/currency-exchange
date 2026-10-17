package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRateDao {
    List<ExchangeRate> findAll();

    ExchangeRate findByPair(String baseCode, String targetCode);

    ExchangeRate save(ExchangeRate exchangeRate);

    ExchangeRate update(String baseCode, String targetCode, BigDecimal newRate);

}
