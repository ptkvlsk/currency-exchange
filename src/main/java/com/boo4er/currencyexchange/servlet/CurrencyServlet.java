package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.mapper.ResponseMapper;
import com.boo4er.currencyexchange.model.Currency;
import com.boo4er.currencyexchange.service.CurrencyService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/currency/*")

public class CurrencyServlet extends BaseServlet {

    private CurrencyService currencyService;

    @Override
    public void init() {
        currencyService = getService(CurrencyService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            throw new ValidationException("Код валюты отсутствует в адресе");
        }

        String code = pathInfo.substring(1);

        Currency currency = currencyService.getByCode(code);
        sendJson(resp, SC_OK, ResponseMapper.toCurrencyResponse(currency));
    }
}
