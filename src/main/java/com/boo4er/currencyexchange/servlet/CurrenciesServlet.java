package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.dao.CurrencyDao;
import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.model.Currency;
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

    private final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Currency> currencies = currencyDao.findAll();
            sendJson(resp, SC_OK, currencies);
        } catch (AppException e) {
            handleException(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String name = req.getParameter("name");
            String code = req.getParameter("code");
            String sign = req.getParameter("sign");

            if (name == null || name.isEmpty()) {
                throw new ValidationException("Отсутствует поле name");
            }
            if (code == null || code.isEmpty()) {
                throw new ValidationException("Отсутствует поле code");
            }
            if (sign == null || sign.isEmpty()) {
                throw new ValidationException("Отсутствует поле sign");
            }

            Currency currency = new Currency();
            currency.setCode(code);
            currency.setFullName(name);
            currency.setSign(sign);

            Currency saved = currencyDao.save(currency);
            sendJson(resp, SC_CREATED, saved);
        } catch (AppException e) {
            handleException(resp, e);
        }

    }
}
