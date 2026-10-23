package com.boo4er.currencyexchange.service;

import com.boo4er.currencyexchange.dao.ExchangeRateDao;
import com.boo4er.currencyexchange.exception.NotFoundException;
import com.boo4er.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {
    private static final String CROSS_CURRENCY = "USD";
    private static final int RATE_SCALE = 6;
    private static final int AMOUNT_SCALE = 2;

    private final ExchangeRateDao exchangeRateDao;

    public ExchangeService(ExchangeRateDao exchangeRateDao) {
        this.exchangeRateDao = exchangeRateDao;
    }

    public ExchangeResult exchange(String from, String to, BigDecimal amount) {
        ExchangeRate rate = findRate(from, to);
        BigDecimal convertedAmount = amount.multiply(rate.getRate())
                .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        return new ExchangeResult(rate, amount, convertedAmount);
    }

    private ExchangeRate findRate(String from, String to) {
        return exchangeRateDao.findByPair(from, to)
                .or(() -> findReverse(from, to))
                .or(() -> findCross(from, to))
                .orElseThrow(() -> new NotFoundException("Курс для пары " + from + " " + to + " не найден"));
    }

    private Optional<ExchangeRate> findReverse(String from, String to) {
        return exchangeRateDao.findByPair(to, from).map(reverse ->
                new ExchangeRate(
                        reverse.getTargetCurrency(),
                        reverse.getBaseCurrency(),
                        BigDecimal.ONE.divide(reverse.getRate(), RATE_SCALE, RoundingMode.HALF_UP)));

    }

    private Optional<ExchangeRate> findCross(String from, String to) {
        Optional<ExchangeRate> crossToFrom = exchangeRateDao.findByPair(CROSS_CURRENCY, from);
        Optional<ExchangeRate> crossToTarget = exchangeRateDao.findByPair(CROSS_CURRENCY, to);

        if (crossToFrom.isEmpty() || crossToTarget.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate base = crossToFrom.get();
        ExchangeRate target = crossToTarget.get();

        return Optional.of(new ExchangeRate(
                base.getTargetCurrency(),
                target.getTargetCurrency(),
                target.getRate().divide(base.getRate(), RATE_SCALE, RoundingMode.HALF_UP)));
    }
}
