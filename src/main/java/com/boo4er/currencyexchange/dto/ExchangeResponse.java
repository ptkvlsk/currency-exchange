package com.boo4er.currencyexchange.dto;

import java.math.BigDecimal;

public record ExchangeResponse(CurrencyResponse baseCurrency,
                               CurrencyResponse targetCurrency,
                               BigDecimal rate,
                               BigDecimal amount,
                               BigDecimal convertedAmount) {

}
