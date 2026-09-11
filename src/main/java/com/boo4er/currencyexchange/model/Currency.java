package com.boo4er.currencyexchange.model;

public class Currency {
    private int id;
    private String code;
    private String fullName;
    private String sign;

    public Currency() {
    }

    public Currency(int id, String code, String fullName, String sign) {
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;

    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getFullName() {
        return fullName;
    }

    public String getSign() {
        return sign;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return String.format("Currency{id=%d, code=%s, fullName=%s, sign=%s}", id, code, fullName, sign);
    }
}

