package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.mapper.ResponseMapper;
import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.service.CurrencyService;
import com.boo4er.currencyexchange.validation.RequestValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static jakarta.servlet.http.HttpServletResponse.SC_CREATED;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/currencies")
public class CurrenciesServlet extends BaseServlet {

    private CurrencyService currencyService;

    @Override
    public void init() {
        currencyService = getService(CurrencyService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Currency> currencies = currencyService.getAllCurrencies();
        sendJson(resp, SC_OK, ResponseMapper.toCurrencyResponseList(currencies));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = RequestValidator.requireText(req.getParameter("name"), "name");
        String code = RequestValidator.requireCurrencyCode(req.getParameter("code"), "code");
        String sign = RequestValidator.requireSign(req.getParameter("sign"), "sign");

        Currency currency = new Currency();
        currency.setCode(code);
        currency.setFullName(name);
        currency.setSign(sign);

        Currency saved = currencyService.create(currency);
        sendJson(resp, SC_CREATED, ResponseMapper.toCurrencyResponse(saved));

    }
}
