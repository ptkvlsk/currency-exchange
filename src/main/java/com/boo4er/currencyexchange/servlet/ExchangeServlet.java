package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.dao.ExchangeRateDao;
import com.boo4er.currencyexchange.dao.JdbcExchangeRateDao;
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
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/exchange")
public class ExchangeServlet extends BaseServlet {

    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {

            String from = req.getParameter("from");
            String to = req.getParameter("to");
            String amountStr = req.getParameter("amount");

            requireNonBlank(from, "from");
            requireNonBlank(to, "to");
            requireNonBlank(amountStr, "amount");

            BigDecimal amount;
            try {
                amount = new BigDecimal(amountStr);
            } catch (NumberFormatException e) {
                throw new ValidationException("Поле amount должно быть числом");
            }

            ExchangeRate rate = findRateForExchange(from, to);
            BigDecimal convertedAmount = amount.multiply(rate.getRate()).setScale(2, RoundingMode.HALF_UP);

            ExchangeResponse response = new ExchangeResponse(rate, amount, convertedAmount);

            sendJson(resp, SC_OK, response);

        } catch (AppException e) {
            handleException(resp, e);
        }
    }

    private ExchangeRate findRateForExchange(String from, String to) {
        return exchangeRateDao.findByPair(from, to)
                .or(() -> findReverse(from, to))
                .or(() -> findCross(from, to))
                .orElseThrow(() -> new NotFoundException("Курс для пары " + from + " " + to + " е найден"));
    }

    private Optional<ExchangeRate> findReverse(String from, String to) {
        return exchangeRateDao.findByPair(to, from).map(reverse -> {
            ExchangeRate result = new ExchangeRate();
            result.setBaseCurrency(reverse.getTargetCurrency());
            result.setTargetCurrency(reverse.getBaseCurrency());
            result.setRate(BigDecimal.ONE.divide(reverse.getRate(), 6, RoundingMode.HALF_UP));
            return result;
        });
    }

    private Optional<ExchangeRate> findCross(String from, String to) {
        Optional<ExchangeRate> usdToFrom = exchangeRateDao.findByPair("USD", from);
        Optional<ExchangeRate> usdToTarget = exchangeRateDao.findByPair("USD", to);

        if (usdToFrom.isEmpty() || usdToTarget.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate base = usdToFrom.get();
        ExchangeRate target = usdToTarget.get();

        ExchangeRate crossRate = new ExchangeRate();
        crossRate.setBaseCurrency(base.getTargetCurrency());
        crossRate.setTargetCurrency(target.getTargetCurrency());
        crossRate.setRate(target.getRate().divide(base.getRate(), 6, RoundingMode.HALF_UP));
        return Optional.of(crossRate);
    }
}
