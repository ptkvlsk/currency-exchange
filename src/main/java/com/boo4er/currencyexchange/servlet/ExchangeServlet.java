package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.dto.ExchangeResponse;
import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.service.ExchangeResult;
import com.boo4er.currencyexchange.service.ExchangeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;

import static jakarta.servlet.http.HttpServletResponse.SC_OK;

@WebServlet("/exchange")
public class ExchangeServlet extends BaseServlet {

    private ExchangeService exchangeService;

    @Override
    public void init() {
        exchangeService = getService(ExchangeService.class);
    }

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

            ExchangeResult result = exchangeService.exchange(from, to, amount);
            ExchangeResponse response = new ExchangeResponse(result.rate(), result.amount(), result.convertedAmount());

            sendJson(resp, SC_OK, response);

        } catch (AppException e) {
            handleException(resp, e);
        }
    }

}
