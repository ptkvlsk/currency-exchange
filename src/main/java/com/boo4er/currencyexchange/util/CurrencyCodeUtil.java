package com.boo4er.currencyexchange.util;

import java.util.Locale;

public class CurrencyCodeUtil {

    private CurrencyCodeUtil() {

    }

    public static String normalize(String code) {
        if (code == null) {
            return null;
        }
        return code.trim().toUpperCase(Locale.ROOT);
    }
}

