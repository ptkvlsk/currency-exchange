package com.boo4er.currencyexchange.service;

import com.boo4er.currencyexchange.dao.ExchangeRateDao;
import com.boo4er.currencyexchange.exception.NotFoundException;
import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeRateService {

    private final ExchangeRateDao exchangeRateDao;
    private final CurrencyService currencyService;

    public ExchangeRateService(ExchangeRateDao exchangeRateDao, CurrencyService currencyService) {
        this.exchangeRateDao = exchangeRateDao;
        this.currencyService = currencyService;
    }

    public List<ExchangeRate> getAll() {
        return exchangeRateDao.findAll();
    }

    public ExchangeRate getByPair(String baseCode, String targetCode) {
        return exchangeRateDao.findByPair(baseCode, targetCode)
                .orElseThrow(() -> rateNotFound(baseCode, targetCode));
    }

    public ExchangeRate create(String baseCode, String targetCode, BigDecimal rate) {
        Currency baseCurrency = currencyService.getByCode(baseCode);
        Currency targetCurrency = currencyService.getByCode(targetCode);

        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);

        return exchangeRateDao.save(exchangeRate);
    }

    public ExchangeRate updateRate(String baseCode, String targetCode, BigDecimal newRate) {
        return exchangeRateDao.update(baseCode, targetCode, newRate)
                .orElseThrow(() -> rateNotFound(baseCode, targetCode));
    }

    private NotFoundException rateNotFound(String baseCode, String targetCode) {
        return new NotFoundException("Курс для пары " + baseCode
                + " " + targetCode + " не найден");
    }
}
