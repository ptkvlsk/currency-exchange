package com.boo4er.currencyexchange.service;

import com.boo4er.currencyexchange.dao.CurrencyDao;
import com.boo4er.currencyexchange.exception.NotFoundException;
import com.boo4er.currencyexchange.model.Currency;

import java.util.List;

public class CurrencyService {

    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public List<Currency> getAllCurrencies() {
        return currencyDao.findAll();
    }

    public Currency getByCode(String code) {
        return currencyDao.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Валюта с кодом " + code + " не найдена"));
    }

    public Currency create(Currency currency) {
        return currencyDao.save(currency);
    }
}
