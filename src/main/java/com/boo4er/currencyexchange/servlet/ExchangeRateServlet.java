package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.dao.ExchangeRateDao;
import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.model.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/exchangeRate/*")

public class ExchangeRateServlet extends BaseServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    private String[] extractPair(HttpServletRequest req) throws ValidationException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            throw new ValidationException("Валютная пара отсутствует в адресе");
        }

        String pair = pathInfo.substring(1);
        if (pair.length() != 6) {
            throw new ValidationException("Некорректная валютная пара");
        }
        return new String[]{pair.substring(0, 3), pair.substring(3)};
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String[] pair = extractPair(req);
            ExchangeRate rate = exchangeRateDao.findByPair(pair[0], pair[1]);
            sendJson(resp, SC_OK, rate);
        } catch (AppException e) {
            handleException(resp, e);
        }

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String[] pair = extractPair(req);

            String rateStr = req.getParameter("rate");
            if (rateStr == null || rateStr.isEmpty()) {
                throw new ValidationException("Отсутствует поле rate");
            }

            BigDecimal rate;
            try {
                rate = new BigDecimal(rateStr);
            } catch (NumberFormatException e) {
                throw new ValidationException("Поле rate должно быть числом");
            }

            ExchangeRate updated = exchangeRateDao.update(pair[0], pair[1], rate);
            sendJson(resp, SC_OK, updated);
        } catch (AppException e) {
            handleException(resp, e);
        }
    }

}
