package com.boo4er.currencyexchange.dto;

import com.boo4er.currencyexchange.model.Currency;

public class CurrencyResponse {

    private int id;
    private String name;
    private String code;
    private String sign;

    public CurrencyResponse() {
    }

    public CurrencyResponse(Currency currency) {
        this.id = currency.getId();
        this.name = currency.getFullName();
        this.code = currency.getCode();
        this.sign = currency.getSign();
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getCode() {
        return code;
    }
    public String getSign() {
        return sign;
    }
}
