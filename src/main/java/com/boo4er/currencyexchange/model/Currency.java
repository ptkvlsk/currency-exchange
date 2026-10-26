package com.boo4er.currencyexchange.model;

public final class Currency {
    private final int id;
    private final String code;
    private final String fullName;
    private final String sign;

    public Currency(String code, String fullName, String sign) {
        this(0, code, fullName, sign);
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



    @Override
    public String toString() {
        return String.format("Currency{id=%d, code=%s, fullName=%s, sign=%s}", id, code, fullName, sign);
    }
}

