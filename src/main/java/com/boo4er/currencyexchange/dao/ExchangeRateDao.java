package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateDao {



    List<ExchangeRate> findAll();

    Optional<ExchangeRate> findByPair(String baseCode, String targetCode);

    ExchangeRate save(ExchangeRate exchangeRate);

    Optional<ExchangeRate> update(String baseCode, String targetCode, BigDecimal newRate);

}
