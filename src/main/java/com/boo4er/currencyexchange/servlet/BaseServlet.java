package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.dto.ErrorResponse;
import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.util.JsonUtil;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {

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
            sendError(resp, appException);
        } else {
            e.printStackTrace();
            sendJson(resp, 500, new ErrorResponse("Internal Server Error"));
        }
    }
}
