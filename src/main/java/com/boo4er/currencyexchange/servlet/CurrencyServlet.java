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

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/currency/*")

public class CurrencyServlet extends BaseServlet {

    private final CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                throw new ValidationException("Код валюты отсутствует в адресе");
            }

            String code = pathInfo.substring(1);

            Currency currency = currencyDao.findByCode(code);
            sendJson(resp, SC_OK, currency);
        }catch (AppException e){
            handleException(resp, e);
        }
    }
}
