package com.boo4er.currencyexchange.servlet;

import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {


    protected void sendJson(HttpServletResponse resp, int statusCode, Object obj) throws IOException {
        String json;
        try {
            json = JsonUtil.toJson(obj);
        } catch (Exception e) {
            throw new IOException("Ошибка при сериализации в JSON", e);
        }

        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    protected void requireNonBlank(String value, String field) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException("Отсутствует поле " + field);
        }
    }

    protected <T> T getService(Class<T> type) {
        Object service = getServletContext().getAttribute(type.getName());
        if (service == null) {
            throw new IllegalStateException("Сервис " + type.getSimpleName() + " не найден в ServletContext");
        }
        return type.cast(service);
    }
}
