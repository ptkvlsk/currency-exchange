package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.dao.CurrencyDao;
import com.boo4er.currencyexchange.dao.ExchangeRateDao;
import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.model.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/exchangeRates")

public class ExchangeRatesServlet extends BaseServlet {

    private final CurrencyDao currencyDao = new CurrencyDao();
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ExchangeRate> rates = exchangeRateDao.findAll();

            sendJson(resp, SC_OK, rates);
        } catch (AppException e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String baseCode = req.getParameter("baseCurrencyCode");
            String targetCode = req.getParameter("targetCurrencyCode");
            String rateStr = req.getParameter("rate");

            requireNonBlank(baseCode, "baseCurrencyCode");
            requireNonBlank(targetCode, "targetCurrencyCode");
            requireNonBlank(rateStr, "rate");

            BigDecimal rate;
            try {
                rate = new BigDecimal(rateStr);
            } catch (NumberFormatException e) {
                throw new ValidationException("Поле rate должно быть числом");
            }

            Currency baseCurrency = currencyDao.findByCode(baseCode);
            Currency targetCurrency = currencyDao.findByCode(targetCode);

            ExchangeRate exchangeRate = new ExchangeRate();

            exchangeRate.setBaseCurrency(baseCurrency);
            exchangeRate.setTargetCurrency(targetCurrency);
            exchangeRate.setRate(rate);

            ExchangeRate saved = exchangeRateDao.save(exchangeRate);

            sendJson(resp, SC_CREATED, saved);
        } catch (AppException e) {
            handleException(resp, e);
        }
    }
}
