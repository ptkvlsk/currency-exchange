package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.mapper.ResponseMapper;
import com.boo4er.currencyexchange.service.ExchangeResult;
import com.boo4er.currencyexchange.service.ExchangeService;
import com.boo4er.currencyexchange.validation.RequestValidator;
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

        String from = RequestValidator.requireCurrencyCode(req.getParameter("from"), "from");
        String to = RequestValidator.requireCurrencyCode(req.getParameter("to"), "to");
        BigDecimal amount = RequestValidator.requirePositiveNumber(req.getParameter("amount"), "amount");

        ExchangeResult result = exchangeService.exchange(from, to, amount);

        sendJson(resp, SC_OK, ResponseMapper.toExchangeResponse(result));
    }

}
