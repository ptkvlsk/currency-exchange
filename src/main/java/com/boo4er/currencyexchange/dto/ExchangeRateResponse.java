package com.boo4er.currencyexchange.dto;

import com.boo4er.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;

public class ExchangeRateResponse {

    private int id;
    private CurrencyResponse baseCurrency;
    private CurrencyResponse targetCurrency;
    private BigDecimal rate;

    public ExchangeRateResponse(){
    }

    public ExchangeRateResponse(ExchangeRate exchangeRate) {
        this.id = exchangeRate.getId();
        this.baseCurrency = new CurrencyResponse(exchangeRate.getBaseCurrency());
        this.targetCurrency = new CurrencyResponse(exchangeRate.getTargetCurrency());
        this.rate = exchangeRate.getRate();
    }
    public int getId() {
        return id;
    }

    public CurrencyResponse getBaseCurrency() {
        return baseCurrency;
    }

    public CurrencyResponse getTargetCurrency() {
        return targetCurrency;
    }
    public BigDecimal getRate() {
        return rate;
    }

}
