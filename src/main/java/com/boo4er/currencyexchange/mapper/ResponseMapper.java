package com.boo4er.currencyexchange.mapper;

import com.boo4er.currencyexchange.dto.CurrencyResponse;
import com.boo4er.currencyexchange.dto.ExchangeRateResponse;
import com.boo4er.currencyexchange.dto.ExchangeResponse;
import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.model.ExchangeRate;
import com.boo4er.currencyexchange.service.ExchangeResult;

import java.util.List;

public final class ResponseMapper {

    private ResponseMapper() {

    }

    public static CurrencyResponse toCurrencyResponse(Currency currency) {
        return new CurrencyResponse(currency);
    }

    public static List<CurrencyResponse> toCurrencyResponseList(List<Currency> currencies) {
        return currencies.stream().map(ResponseMapper::toCurrencyResponse).toList();
    }

    public static ExchangeRateResponse toExchangeRateResponse(ExchangeRate rate) {
        return new ExchangeRateResponse(rate);
    }

    public static List<ExchangeRateResponse> toExchangeRateResponseList(List<ExchangeRate> rates) {
        return rates.stream().map(ResponseMapper::toExchangeRateResponse).toList();
    }

    public static ExchangeResponse toExchangeResponse(ExchangeResult result) {
        return new ExchangeResponse(result.rate(), result.amount(), result.convertedAmount());
    }
}
