package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.model.Currency;

import java.util.List;

public interface CurrencyDao {
    List<Currency> findAll();

    Currency findByCode(String code);

    Currency save(Currency currency);

}
