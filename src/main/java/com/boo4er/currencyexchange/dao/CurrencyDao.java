package com.boo4er.currencyexchange.dao;

import com.boo4er.currencyexchange.model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyDao {

    Optional<Currency> findByCode(String code);

    List<Currency> findAll();


    Currency save(Currency currency);

}
