package com.boo4er.currencyexchange.validation;

import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.util.CurrencyCodeUtil;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class RequestValidator {

    private static final Pattern CURRENCY_CODE_PATTERN = Pattern.compile("[A-Z]{3}");
    private static final int MAX_SIGN_LENGTH = 3;

    private RequestValidator() {
    }

    public static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Отсутствует поле " + field);
        }
        return value.trim();
    }

    public static String requireCurrencyCode(String value, String field) {
        String code = CurrencyCodeUtil.normalize(requireText(value, field));
        if (!CURRENCY_CODE_PATTERN.matcher(code).matches()) {
            throw new ValidationException("Поле " + field + " должно состоять из трёх латинских букв");
        }
        return code;
    }

    public static String requireSign(String value, String field) {
        String sign = requireText(value, field);
        if (sign.length() > MAX_SIGN_LENGTH) {
            throw new ValidationException("Поле " + field + " должно быть не длинее " + MAX_SIGN_LENGTH + " символов");
        }
        return sign;
    }

    public static BigDecimal requirePositiveNumber(String value, String field) {
        String text = requireText(value, field);

        BigDecimal number;
        try {
            number = new BigDecimal(text);
        } catch (NumberFormatException e) {
            throw new ValidationException("Поле " + field + " должно быть числом");
        }
        if (number.signum() <= 0) {
            throw new ValidationException("Поле " + field + " должно быть больше нуля");
        }
        return number;
    }

    public static void requireDifferent(String first, String second, String message) {
        if (first.equals(second)) {
            throw new ValidationException(message);
        }
    }
}
