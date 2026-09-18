package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.dao.ExchangeRateDao;
import com.boo4er.currencyexchange.dto.ExchangeResponse;
import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.exception.NotFoundException;
import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.model.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/exchange")
public class ExchangeServlet extends BaseServlet {

    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            String from = req.getParameter("from");
            String to = req.getParameter("to");
            String amountStr = req.getParameter("amount");

            if (from == null || from.isEmpty()) {
                throw new ValidationException("Отсутствует параметр from");
            }
            if (to == null || to.isEmpty()) {
                throw new ValidationException("Отсутствует параметр to");
            }
            if (amountStr == null || amountStr.isEmpty()) {
                throw new ValidationException("Отсутствует параметр amount");
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                throw new ValidationException("Поле amount должно быть числом");
            }

            ExchangeRate rate = findRateForExchange(from, to);
            BigDecimal convertedAmount = amount.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_UP);

            ExchangeResponse response = new ExchangeResponse(
                    rate.getBaseCurrency(), rate.getTargetCurrency(), rate.getRate(), amount, convertedAmount);

            sendJson(resp, SC_OK, response);

        } catch (AppException e) {
            handleException(resp, e);
        }
    }

    private ExchangeRate findRateForExchange(String from, String to) throws AppException {
        try {
            return exchangeRateDao.findByPair(from, to);
        } catch (NotFoundException e) {
        }
        try {
            ExchangeRate reverse = exchangeRateDao.findByPair(to, from);
            ExchangeRate reverseRate = new ExchangeRate();

            reverseRate.setBaseCurrency(reverse.getTargetCurrency());
            reverseRate.setTargetCurrency(reverse.getBaseCurrency());
            reverseRate.setRate(BigDecimal.ONE.divide(reverse.getRate(), 6, RoundingMode.HALF_UP));
            return reverseRate;
        } catch (NotFoundException e) {
        }
        try {
            ExchangeRate usdToFrom = exchangeRateDao.findByPair("USD", from);
            ExchangeRate usdToTarget = exchangeRateDao.findByPair("USD", to);

            ExchangeRate crossRate = new ExchangeRate();
            crossRate.setBaseCurrency(usdToFrom.getTargetCurrency());
            crossRate.setTargetCurrency(usdToTarget.getTargetCurrency());
            crossRate.setRate(usdToTarget.getRate().divide(usdToFrom.getRate(), 6, RoundingMode.HALF_UP));
            return crossRate;
        } catch (NotFoundException e) {
        }

        throw new NotFoundException("Курс для пары " + from + " " + to + " не найден");
    }
}
