package com.boo4er.currencyexchange.filter;

import com.boo4er.currencyexchange.dto.ErrorResponse;
import com.boo4er.currencyexchange.exception.AppException;
import com.boo4er.currencyexchange.exception.AlreadyExistsException;
import com.boo4er.currencyexchange.exception.NotFoundException;
import com.boo4er.currencyexchange.exception.ValidationException;
import com.boo4er.currencyexchange.util.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingFilter.class);
    private static final String INTERNAL_ERROR_MESSAGE = "Internal Server Error";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse resp = (HttpServletResponse) response;
        try {
            chain.doFilter(request, response);
        } catch (AppException e) {
            handleAppException(resp, e);
        } catch (RuntimeException e) {
            log.error("Неожиданная ошибка при обработке запроса", e);
            sendError(resp, SC_INTERNAL_SERVER_ERROR, INTERNAL_ERROR_MESSAGE);
        }
    }

    private void handleAppException(HttpServletResponse resp, AppException e) throws IOException {
        int status = resolveStatus(e);
        if (status == SC_INTERNAL_SERVER_ERROR) {
            log.error("Внутренняя ошибка приложения", e);
            sendError(resp, status, INTERNAL_ERROR_MESSAGE);
        } else {
            log.warn("Ошибка обработки запроса: {}", e.getMessage());
            sendError(resp, status, e.getMessage());
        }
    }

    private int resolveStatus(AppException e) {
        if (e instanceof ValidationException) {
            return SC_BAD_REQUEST;
        }
        if (e instanceof NotFoundException) {
            return SC_NOT_FOUND;
        }
        if (e instanceof AlreadyExistsException) {
            return SC_CONFLICT;
        }
        return SC_INTERNAL_SERVER_ERROR;
    }

    private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
        String json;
        try {
            json = JsonUtil.toJson(new ErrorResponse(message));
        } catch (Exception e) {
            throw new IOException("Ошибка при сериализации в JSON", e);
        }

        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
