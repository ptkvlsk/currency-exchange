package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.dto.ErrorResponse;
import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(BaseServlet.class);


    protected void sendJson(HttpServletResponse resp, int statusCode, Object obj) throws IOException {

        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String json = JsonUtil.toJson(obj);
            resp.getWriter().write(json);
        } catch (Exception e) {
            throw new IOException("Ошибка при сериализации в JSON", e);
        }
    }

    protected void sendError(HttpServletResponse resp, AppException e) throws IOException {
        sendJson(resp, e.getStatusCode(), new ErrorResponse(e.getMessage()));
    }

    protected void handleException(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof AppException appException) {
            log.warn("Ошибка обработки запроса: {}", e.getMessage());
            sendError(resp, appException);
        } else {
            log.error("Неожиданная ошибка при обработке запроса", e);
            sendJson(resp, 500, new ErrorResponse("Internal Server Error"));
        }
    }
}
