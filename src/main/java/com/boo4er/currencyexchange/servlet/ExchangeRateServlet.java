package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.mapper.ResponseMapper;
import com.boo4er.currencyexchange.model.ExchangeRate;
import com.boo4er.currencyexchange.service.ExchangeRateService;
import com.boo4er.currencyexchange.validation.RequestValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/exchangeRate/*")

public class ExchangeRateServlet extends BaseServlet {

    private ExchangeRateService exchangeRateService;

    @Override
    public void init() {
        exchangeRateService = getService(ExchangeRateService.class);
    }

    private String[] extractPair(HttpServletRequest req) throws ValidationException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new ValidationException("Валютная пара отсутствует в адресе");
        }

        String pair = pathInfo.substring(1);
        if (pair.length() != 6) {
            throw new ValidationException("Некорректная валютная пара");
        }
        String base = RequestValidator.requireCurrencyCode(pair.substring(0, 3), "baseCurrencyCode");
        String target = RequestValidator.requireCurrencyCode(pair.substring(3), "targetCurrencyCode");
        return new String[]{base, target};
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] pair = extractPair(req);
        ExchangeRate rate = exchangeRateService.getByPair(pair[0], pair[1]);
        sendJson(resp, SC_OK, ResponseMapper.toExchangeRateResponse(rate));

    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] pair = extractPair(req);

        BigDecimal rate = RequestValidator.requirePositiveNumber(req.getParameter("rate"), "rate");

        ExchangeRate updated = exchangeRateService.updateRate(pair[0], pair[1], rate);
        sendJson(resp, SC_OK, ResponseMapper.toExchangeRateResponse(updated));
    }
}
