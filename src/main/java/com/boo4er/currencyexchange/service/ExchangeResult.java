package com.boo4er.currencyexchange.service;

import com.boo4er.currencyexchange.model.ExchangeRate;

import java.math.BigDecimal;

public record ExchangeResult(ExchangeRate rate, BigDecimal amount, BigDecimal convertedAmount) {
}
