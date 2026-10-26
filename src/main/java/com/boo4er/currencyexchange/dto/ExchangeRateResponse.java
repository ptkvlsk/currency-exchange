package com.boo4er.currencyexchange.dto;

import java.math.BigDecimal;

public record ExchangeRateResponse(int id,
                                   CurrencyResponse baseCurrency,
                                   CurrencyResponse targetCurrency,
                                   BigDecimal rate) {
}
