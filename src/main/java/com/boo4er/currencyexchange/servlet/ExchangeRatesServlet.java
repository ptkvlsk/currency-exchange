package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.mapper.ResponseMapper;
import com.boo4er.currencyexchange.model.ExchangeRate;
import com.boo4er.currencyexchange.service.ExchangeRateService;
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

    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        exchangeRateService = getService(ExchangeRateService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRate> rates = exchangeRateService.getAll();
        sendJson(resp, SC_OK, ResponseMapper.toExchangeRateResponseList(rates));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        ExchangeRate saved = exchangeRateService.create(baseCode, targetCode, rate);

        sendJson(resp, SC_CREATED, ResponseMapper.toExchangeRateResponse(saved));
    }
}
